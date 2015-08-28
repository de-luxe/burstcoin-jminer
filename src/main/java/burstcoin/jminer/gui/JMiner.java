/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 by luxe - https://github.com/de-luxe - BURST-LUXE-ZDVD-CX3E-3SM58
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

package burstcoin.jminer.gui;

import burstcoin.jminer.gui.mining.MiningController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JMiner
  extends Application
{
  private JMinerView view;
  private MiningController miningController;

  @Override
  public void stop()
    throws Exception
  {

    super.stop();
    System.exit(0);
  }

  @Override
  public void start(Stage primaryStage)
    throws Exception
  {
//    ConfigurableApplicationContext applicationContext = SpringApplication.run(CommandLineRunner.class, "");

    // that means i can just fire within spring context and gui will receive that!
//    applicationContext.addApplicationListener(new ApplicationListener<NetworkResultConfirmedEvent>()
//    {
//      @Override
//      public void onApplicationEvent(NetworkResultConfirmedEvent event)
//      {
//
//      }
//    });

    // that means i can just fire within spring context and gui will receive that!
//    applicationContext.addApplicationListener(new ApplicationListener<CheckerResultEvent>()
//    {
//      @Override
//      public void onApplicationEvent(CheckerResultEvent checkerResultEvent)
//      {
//
//      }
//    });

    view = new JMinerView(new JMinerView.Action()
    {
      @Override
      public void onTabSelect(String name)
      {
//                System.out.println(name);
      }
    });

    initializeMiner();

    view.addTo(primaryStage);

  }

  private void initializeMiner()
  {

    FXMLLoader loader = new FXMLLoader();
    // set fxml location
    loader.setLocation(getClass().getResource("mining/mining.fxml"));
    // load and add
    Node node = null;
    try
    {
      node = loader.load();
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
    String uuid = view.addContent("mining", node);
    // get controller from loader
    miningController = loader.getController();

    view.showTab(uuid);
  }
}
