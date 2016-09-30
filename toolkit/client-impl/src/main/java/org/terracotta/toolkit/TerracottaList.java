package org.terracotta.toolkit;

import org.terracotta.entity.EntityClientEndpoint;
import org.terracotta.entity.InvokeFuture;
import org.terracotta.entity.MessageCodecException;
import org.terracotta.exception.EntityException;
import org.terracotta.toolkit.list.ListCodec;
import org.terracotta.toolkit.list.ListConfig;
import org.terracotta.toolkit.list.ListRequest;
import org.terracotta.toolkit.list.ListResponse;
import org.terracotta.toolkit.list.ToolkitList;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TerracottaList implements ToolkitList {
  private final String name;
  private final String type;
  private final EntityClientEndpoint<ToolkitMessage, ToolkitResponse> endpoint;
  private final TerracottaToolkit toolkit;
  private final ReadWriteLock closeLock;
  private boolean closed = false;
  private final OperationTarget<ListRequest, ListResponse> opTar;
  private final ListCodec codec = new ListCodec();

  public TerracottaList(TerracottaToolkit owner, EntityClientEndpoint<ToolkitMessage, ToolkitResponse> endpoint, String type, String name, ListConfig config) {
    this.toolkit = owner;
    this.name = name;
    this.type = type;
    this.endpoint = endpoint;
    this.closeLock = new ReentrantReadWriteLock();

    this.opTar = new OperationTarget<ListRequest, ListResponse>(type, name, new ListCodec());
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public byte[] createReconnectData() {
    return new byte[0];
  }

  @Override
  public void handleServerMessage(final ToolkitResponse data) {

  }

  @Override
  public void didDisconnectUnexpectedly() {

  }


  @Override
  public void close() throws Exception {

  }

  @Override
  public int size() {
    try {
      ListRequest request = new ListRequest.SizeMessage();
      InvokeFuture<ToolkitResponse> future = endpoint.beginInvoke().message(opTar.target(request)).invoke();
      ListResponse.Size response = (ListResponse.Size)opTar.process(future.get());
      return response.getSize();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } catch (MessageCodecException e) {
      throw new RuntimeException(e);
    } catch (EntityException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean isEmpty() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean contains(final Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Iterator iterator() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object[] toArray() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object[] toArray(final Object[] a) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean add(Object o) {
    try {
      ListRequest request = new ListRequest.AddMessage(codec.serializeElement(o));
      InvokeFuture<ToolkitResponse> future = endpoint.beginInvoke().message(opTar.target(request)).invoke();
      ListResponse.Add response = (ListResponse.Add)opTar.process(future.get());
      return toBoolean(response.getFlag());
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    } catch (MessageCodecException e) {
      throw new RuntimeException(e);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } catch (EntityException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean remove(final Object o) {
    try {
      ListRequest request = new ListRequest.RemoveMessage(codec.serializeElement(o));
      InvokeFuture<ToolkitResponse> future = endpoint.beginInvoke().message(opTar.target(request)).invoke();
      ListResponse.Remove response = (ListResponse.Remove)opTar.process(future.get());
      return toBoolean(response.getFlag());
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } catch (MessageCodecException e) {
      throw new RuntimeException(e);
    } catch (EntityException e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  public boolean addAll(final Collection c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addAll(final int index, final Collection c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(final Collection c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(final Collection c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsAll(final Collection c) {
    throw new UnsupportedOperationException();
  }


  public Object get(int index) {
    try {
      ListRequest request = new ListRequest.GetMessage(index);
      InvokeFuture<ToolkitResponse> future = endpoint.beginInvoke().message(opTar.target(request)).invoke();
      ListResponse.Get response = (ListResponse.Get)opTar.process(future.get());
      if (response.getElement() != null && response.getElement().length > 0) {
        return codec.deserializeElement(response.getElement());
      }
    } catch (MessageCodecException e) {
      throw new RuntimeException(e);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } catch (EntityException e) {
      throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return null;
  }

  @Override
  public Object set(final int index, final Object element) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void add(final int index, final Object element) {
    try {
      ListRequest request = new ListRequest.AddAtMessage(codec.serializeElement(element), index);
      InvokeFuture<ToolkitResponse> future = endpoint.beginInvoke().message(opTar.target(request)).invoke();
      ListResponse response = opTar.process(future.get());
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } catch (MessageCodecException e) {
      throw new RuntimeException(e);
    } catch (EntityException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Object remove(final int index) {
    try {
      ListRequest request = new ListRequest.RemoveAtMessage(index);
      InvokeFuture<ToolkitResponse> future = endpoint.beginInvoke().message(opTar.target(request)).invoke();
      ListResponse.RemoveAt response = (ListResponse.RemoveAt)opTar.process(future.get());
      if (response.getElement() != null) {
        return codec.deserializeElement(response.getElement());
      } else {
        return null;
      }

    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } catch (MessageCodecException e) {
      throw new RuntimeException(e);
    } catch (EntityException e) {
      throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int indexOf(final Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int lastIndexOf(final Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ListIterator listIterator() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ListIterator listIterator(final int index) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List subList(final int fromIndex, final int toIndex) {
    throw new UnsupportedOperationException();
  }

  private boolean toBoolean(int i) {
    return (i != 0);
  }


}
