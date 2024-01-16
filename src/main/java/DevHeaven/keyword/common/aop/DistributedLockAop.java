package DevHeaven.keyword.common.aop;

import DevHeaven.keyword.common.exception.FriendException;
import DevHeaven.keyword.common.exception.MemberException;
import DevHeaven.keyword.common.exception.type.ErrorCode;
import DevHeaven.keyword.domain.member.dto.MemberAdapter;
import DevHeaven.keyword.domain.member.entity.Member;
import DevHeaven.keyword.domain.member.repository.MemberRepository;
import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAop {

  private final RedissonClient redissonClient;
  private final AopForTransaction aopForTransaction;
  private final MemberRepository memberRepository;

  @Around("@annotation(DevHeaven.keyword.common.aop.DistributedLock)")
  public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
    final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    final Method method = signature.getMethod();
    final DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

    final String key = createKey(signature.getParameterNames(), joinPoint.getArgs(),
        distributedLock.key());

    final RLock lock = redissonClient.getLock(key);

    try{
      boolean isPossible= lock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());

      if(!isPossible){
        return false;
      }

      log.info("redis lock start");
      return aopForTransaction.proceed(joinPoint);
    }catch (InterruptedException e){
      throw new InterruptedException();
    }finally {
      try {
        lock.unlock();
        log.info("redis lock end");
      }catch (IllegalMonitorStateException e){
        log.info("redis unlock already");
        throw new IllegalMonitorStateException();
      }
    }
  }

  private String createKey(final String[] parameterNames, final Object[] args, final String key){
    String resultKey = key;
    Long memberRequestId = null;
    Long friendId = null;
    for (int i = 0; i < parameterNames.length; i++) {
      if (parameterNames[i].equals("memberAdapter")) {
        final MemberAdapter memberAdapter = (MemberAdapter) args[i];
        final Member member = memberRepository.findByEmail(memberAdapter.getEmail())
            .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        memberRequestId =  member.getMemberId();
      } else if (parameterNames[i].equals("friendId")) {
        friendId = (Long) args[i];
      }
    }

    if(memberRequestId == null || friendId == null){
      throw new FriendException(ErrorCode.FRIEND_NOT_VALID_REQUEST);
    }

    resultKey += " : "+ Math.min(memberRequestId,friendId)+ "-" + Math.max(memberRequestId,friendId);
    return resultKey;
  }
}
