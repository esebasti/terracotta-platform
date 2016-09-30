package org.terracotta.toolkit;

import org.terracotta.connection.Connection;
import org.terracotta.connection.ConnectionException;
import org.terracotta.connection.ConnectionFactory;
import org.terracotta.connection.entity.EntityRef;
import org.terracotta.passthrough.IClientTestEnvironment;
import org.terracotta.passthrough.IClusterControl;
import org.terracotta.passthrough.ICommonTest;
import org.terracotta.toolkit.barrier.Barrier;
import org.terracotta.toolkit.barrier.BarrierConfig;
import org.terracotta.toolkit.list.ListConfig;

import com.tc.util.Assert;

import java.net.URI;
import java.util.List;
import java.util.Properties;


public class ToolkitListTest implements ICommonTest {
  @Override
  public void runSetup(IClientTestEnvironment env, IClusterControl control) throws Throwable {

  }

  @Override
  public void runDestroy(IClientTestEnvironment env, IClusterControl control) throws Throwable {

  }

  @SuppressWarnings("resource")
  @Override
  public void runTest(IClientTestEnvironment env, IClusterControl control) throws Throwable {
    URI uri = URI.create(env.getClusterUri());
    Connection connection = null;
    try {
      Properties emptyProperties = new Properties();
      connection = ConnectionFactory.connect(uri, emptyProperties);
    } catch (ConnectionException e) {
      org.terracotta.testing.common.Assert.unexpected(e);
    }
    EntityRef<Toolkit, ToolkitConfig> ref = connection.getEntityRef(Toolkit.class, Toolkit.VERSION, ToolkitConstants.STANDARD_TOOLKIT);
    Toolkit toolkit = ref.fetchEntity();
    Barrier b = toolkit.createBarrier("my-barrier", new BarrierConfig(2));
    if (b == null) {
      b = toolkit.getBarrier("my-barrier");
    }
    Assert.assertNotNull(b);
    System.out.println("parties:" + b.getParties());
    int id = b.await();
    System.out.println("id:" + id);
    List list = null;
    if (id == 0) {
      //list operations by client 0
      list = toolkit.createList("my-list", new ListConfig());
      Assert.assertNotNull(list);
      list.add("element1");
      list.add(1, "element2");
      Assert.assertEquals(list.get(0), "element1");
      Assert.assertEquals(list.get(1), "element2");
      Assert.assertEquals(list.size(), 2);
      Assert.assertTrue(list.remove("element1"));
      Assert.assertEquals(list.size(), 1);
      Assert.assertEquals(list.remove(0), "element2");
      Assert.assertEquals(list.size(), 0);
      list.add("element3");
    }
    //sleep here as barrier.await() hangs when invoked again
    Thread.sleep(10000);
    if (id != 0) {
      //Obtain list created by first client
      //toolkit get doesn't work without preceding create call
      list = toolkit.createList("my-list", new ListConfig());
      Assert.assertNull(list);
      list = toolkit.getList("my-list");
      Assert.assertNotNull(list);
      Assert.assertEquals(list.get(0), "element3");
    }
    //sleep here as barrier.await() hangs when invoked again
    Thread.sleep(10000);
    connection.close();
  }
}