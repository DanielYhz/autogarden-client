package cmpe273.group6.client.Service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledService {
    //    second（秒）   minute（分）, hour（时）, day of month （日）, month（月） day of week（周几）.
    //    e.g. {@code "0 * * * * MON-FRI"}
    //    【0 0/15 14，18 * * ？】 每天14点和18点每隔5分钟执行一次
    //    【0 15 10 ？ * 1-6】 每个月的周一到周五的10点15分执行一次
    //    【0 0 2 ？ * 6L】 每个月的最后一个周六凌晨2点执行一次
    //    【0 0 2 LW * ？】 每个月的最后一个工作日凌晨2点执行一次
    //    【0 0 2-4 ？ * 1#1】 每个月的第一个周一凌晨2点到4点期间，每个整点执行一次

    @Scheduled(cron = "0-4 * * * * *")  //每分钟的前4秒会执行hello方法
    public void chargeUser() {

    }
}
