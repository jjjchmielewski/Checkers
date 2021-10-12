package com.pk.Server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.List;

import com.pk.server.BasicUdpServer;
import com.pk.server.models.Player;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class BasicUdpServerTest {

  @Test
  public void testIfCorrectProbeBeingSend() throws Exception {
    final DatagramSocket socket = mock(DatagramSocket.class);
    BasicUdpServer bUdpServer = new BasicUdpServer(socket);
    ArgumentCaptor<DatagramPacket> packetCaptor = ArgumentCaptor.forClass(DatagramPacket.class);
    doNothing()
    .doThrow(new RuntimeException("send called second time"))
    .when(socket).send(packetCaptor.capture());

    doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) throws SocketTimeoutException {
        throw new SocketTimeoutException();
      }
    })
    .when(socket).receive(any(DatagramPacket.class));

    bUdpServer.findPlayers();
    DatagramPacket dp = packetCaptor.getValue();

    assertEquals(InetAddress.getByName("255.255.255.255"), dp.getAddress());
    assertEquals(10000, dp.getPort());
    assertEquals("checkers:probe", new String(dp.getData()));
  }

  @Test
  public void testParsingCorrectResp() throws Exception {
    // dGVzdA== <-> test
    byte[] bytes = "checkers:probeResp testNik dGVzdA==".getBytes();
    DatagramPacket receivedPacket = new DatagramPacket(bytes, bytes.length, InetAddress.getByName("192.168.0.105"), 12345);
    final DatagramSocket socket = mock(DatagramSocket.class);
    BasicUdpServer bUdpServer = new BasicUdpServer(socket);
    ArgumentCaptor<DatagramPacket> packetCaptor = ArgumentCaptor.forClass(DatagramPacket.class);
    doNothing()
    .doThrow(new RuntimeException("send called second time"))
    .when(socket).send(packetCaptor.capture());

    doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        DatagramPacket dp = (DatagramPacket)args[0];
        dp.setData(receivedPacket.getData(), 0, receivedPacket.getLength());
        dp.setLength(receivedPacket.getLength());
        dp.setAddress(receivedPacket.getAddress());
        dp.setPort(receivedPacket.getPort());
        return null;
      }
    })
    .doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) throws SocketTimeoutException {
        throw new SocketTimeoutException();
      }
    })
    .when(socket).receive(any(DatagramPacket.class));

    List<Player> players = bUdpServer.findPlayers();

    assertEquals(1, players.size());
    Player player = players.get(0);
    assertEquals(InetAddress.getByName("192.168.0.105"), player.getIp());
    assertEquals("testNik", player.getNick());
    assertEquals("dGVzdA==", player.getProfileImg());
  }

  @Test
  public void testParsingInvalidResp() throws Exception {
    // dGVzdA== <-> test
    byte[] bytes = "checkers:probeResptestNik dGVzdA==".getBytes();
    DatagramPacket receivedPacket = new DatagramPacket(bytes, bytes.length, InetAddress.getByName("192.168.0.105"), 12345);
    final DatagramSocket socket = mock(DatagramSocket.class);
    BasicUdpServer bUdpServer = new BasicUdpServer(socket);
    ArgumentCaptor<DatagramPacket> packetCaptor = ArgumentCaptor.forClass(DatagramPacket.class);
    doNothing()
    .doThrow(new RuntimeException("send called second time"))
    .when(socket).send(packetCaptor.capture());

    doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        DatagramPacket dp = (DatagramPacket)args[0];
        dp.setData(receivedPacket.getData(), 0, receivedPacket.getLength());
        dp.setLength(receivedPacket.getLength());
        dp.setAddress(receivedPacket.getAddress());
        dp.setPort(receivedPacket.getPort());
        return null;
      }
    })
    .doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) throws SocketTimeoutException {
        throw new SocketTimeoutException();
      }
    })
    .when(socket).receive(any(DatagramPacket.class));

    List<Player> players = bUdpServer.findPlayers();

    assertEquals(0, players.size());
  }
}
