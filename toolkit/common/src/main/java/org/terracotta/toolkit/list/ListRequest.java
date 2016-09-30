package org.terracotta.toolkit.list;

import org.terracotta.entity.EntityMessage;

public abstract class ListRequest implements EntityMessage {

  protected ListOperation opCode;


  protected ListRequest(final ListOperation opCode) {
    this.opCode = opCode;
  }


  public ListOperation getOpCode() {
    return opCode;
  }

  public static class AddMessage extends ListRequest {
    private byte[] element;

    public AddMessage(final byte[] element) {
      super(ListOperation.ADD);
      this.element = element;
    }

    public byte[] getElement() {
      return element;
    }
  }

  public static class AddAtMessage extends ListRequest {
    private int index;
    private byte[] element;


    public AddAtMessage(final byte[] element, final int index) {
      super(ListOperation.ADD_AT);
      this.element = element;
      this.index = index;
    }

    public byte[] getElement() {
      return element;
    }

    public int getIndex() {
      return index;
    }
  }

  public static class RemoveMessage extends ListRequest {
    private byte[] element;

    public RemoveMessage(final byte[] element) {
      super(ListOperation.REMOVE);
      this.element = element;
    }

    public byte[] getElement() {
      return element;
    }
  }

  public static class RemoveAtMessage extends ListRequest {
    private int index;

    public RemoveAtMessage(final int index) {
      super(ListOperation.REMOVE_AT);
      this.index = index;
    }

    public int getIndex() {
      return index;
    }
  }

  public static class GetMessage extends ListRequest {
    private int index;

    public GetMessage(final int index) {
      super(ListOperation.GET);
      this.index = index;
    }

    public int getIndex() {
      return index;
    }
  }

  public static class SizeMessage extends ListRequest {

    public SizeMessage() {
      super(ListOperation.SIZE);
    }

  }
}
