/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 by luxe - https://github.com/de-luxe - BURST-LUXE-RED2-G6JW-H4HG5
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package burstcoin.jminer;

import burstcoin.jminer.core.CoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class JMinerApplication
{
  private static final Logger LOG = LoggerFactory.getLogger(JMinerApplication.class);

  public static void main(String[] args)
  {
    LOG.info("Starting the engines ... please wait!");

    // overwritten by application.properties
    Map<String, Object> properties = new HashMap<>();
    if(CoreProperties.isWriteLogFile())
    {
      properties.put("logging.file", CoreProperties.getLogFilePath());
    }
    properties.put("logging.level.burstcoin.jminer.core", CoreProperties.isDebug() ? "DEBUG" : "INFO");

    new SpringApplicationBuilder(JMinerApplication.class)
      .bannerMode(Banner.Mode.OFF) // turn off spring boot banner
      .logStartupInfo(false)
      .properties(properties) // add application.properties
      .build(args)
      .run();
  }

  @Bean
  public CommandLineRunner getCommandLineRunner(ConfigurableApplicationContext context)
  {
    return new JMinerCommandLine(context);
  }
}
