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

package burstcoin.jminer.gui.mining;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MiningController
  implements Initializable
{
  public SplitPane roundAndCommittedContainer;
  public SplitPane roundsContainer;
  public BorderPane borderPane;

  private CommittedController committedController;
  private RoundController roundController;
  private RoundsController roundsController;
  private NetworkController networkController;

  @Override
  public void initialize(URL location, ResourceBundle resources)
  {
    initializeNetwork();
    initializeRounds();
    initializeRound();
    initializeCommitted();
  }

  private void initializeRound()
  {
    FXMLLoader loader = new FXMLLoader();
    // set fxml location
    loader.setLocation(getClass().getResource("round.fxml"));
    // load and add
    Node roundNode = null;
    try
    {
      roundNode = loader.load();
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
    // get controller from loader
    roundController = loader.getController();
    roundAndCommittedContainer.getItems().add(roundNode);
  }

  private void initializeRounds()
  {
    FXMLLoader loader = new FXMLLoader();
    // set fxml location
    loader.setLocation(getClass().getResource("rounds.fxml"));
    // load and add
    Node roundsNode = null;
    try
    {
      roundsNode = loader.load();
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
    // get controller from loader
    roundsController = loader.getController();
    roundsContainer.getItems().add(0, roundsNode);
  }

  private void initializeCommitted()
  {
    FXMLLoader loader = new FXMLLoader();
    // set fxml location
    loader.setLocation(getClass().getResource("committed.fxml"));
    // load and add
    Node committedNode = null;
    try
    {
      committedNode = loader.load();
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
    // get controller from loader
    committedController = loader.getController();
    roundAndCommittedContainer.getItems().add(committedNode);
  }

  private void initializeNetwork()
  {
    FXMLLoader loader = new FXMLLoader();
    // set fxml location
    loader.setLocation(getClass().getResource("network.fxml"));
    // load and add
    Node networkNode = null;
    try
    {
      networkNode = loader.load();
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
    // get controller from loader
    networkController = loader.getController();
    borderPane.setTop(networkNode);
  }

  public CommittedController getCommittedController()
  {
    return committedController;
  }

  public RoundController getRoundController()
  {
    return roundController;
  }

  public RoundsController getRoundsController()
  {
    return roundsController;
  }

  public NetworkController getNetworkController()
  {
    return networkController;
  }
}
