package com.seightday.app.monitor

import com.jcraft.jsch.Channel
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import groovy.util.logging.Log4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Log4j
@Component
class AppMonitorService {

	@Value('${ssh.expect}')
	String expect
	@Value('${ssh.user}')
	String user
	@Value('${ssh.password}')
	String password
	@Value('${ssh.host}')
	String host
	@Value('${ssh.port}')
	Integer port
	@Value('${ssh.app.name}')
	String appName
	@Value('${restart.cmd}')
	String restartCmd

	@Scheduled(cron = '${cron}')
    void monitor() {
		boolean isAlive=false
		try {
			JSch jsch = new JSch()
            Session session = jsch.getSession(user, host)
            session.setPort(port)
			session.setPassword(password)
            session.setConfig("StrictHostKeyChecking", "no")
            session.connect(60 * 1000)
            Channel channel = session.openChannel("shell")
            Expect expect = new Expect(channel.getInputStream(),
					channel.getOutputStream())
            channel.connect()
            //Last login: Wed Apr 26 21:09:18 2017 from 1.1.1.11
			//[root@sztmq ~]#
			//expect为分割符，可使用#，也可使用[root@sztmq ~]#
			expect.expect(this.expect)
            log.info(expect.before)

			expect.send("ps -ef | grep java\n")
            expect.expect(this.expect)
			log.info(expect.before)

			def before=expect.before
			def split = before.split('\r\n')

			for (int i = 0; i < split.length; i++) {
				String s = split[i]
				if(s.contains(appName)){
					isAlive=true
				}
			}

			if (isAlive){
				log.info("$appName is alive")
			}else {
				log.info("$appName is not alive, restart it")
                expect.send("$restartCmd\n")
                expect.expect(this.expect)
				log.info(expect.before)
			}

			expect.send("exit\n")
            expect.expectEOF()
			expect.close()
            session.disconnect()
        } catch (Exception e) {
			log.error(null,e)
		}
	}

}
