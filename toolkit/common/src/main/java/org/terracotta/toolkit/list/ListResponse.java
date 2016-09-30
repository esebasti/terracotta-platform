package org.terracotta.toolkit.list;

import org.terracotta.entity.EntityResponse;

public abstract class ListResponse implements EntityResponse {
  protected ListOperation opCode;

  public ListResponse(final ListOperation opCode) {
    this.opCode = opCode;
  }

  public ListOperation getOpCode() {
    return opCode;
  }

  public static class Add extends ListResponse {
    private int flag;

    public Add(int flag) {
      super(ListOperation.ADD);
      this.flag = flag;
    }

    public int getFlag() {
      return flag;
    }
  }

  public static class AddAt extends ListResponse {
    public AddAt() {
      super(ListOperation.ADD_AT);
    }
  }

  public static class Remove extends ListResponse {
    private int flag;

    public Remove(int flag) {
      super(ListOperation.REMOVE);
      this.flag = flag;
    }

    public int getFlag() {
      return flag;
    }
  }

  public static class RemoveAt extends ListResponse {
    byte[] element;

    public RemoveAt(byte[] element) {
      super(ListOperation.REMOVE_AT);
      this.element = element;
    }

    public byte[] getElement() {
      return element;
    }
  }

  public static class Get extends ListResponse {
    private byte[] element;

    public Get(final byte[] element) {
      super(ListOperation.GET);
      this.element = element;
    }

    public byte[] getElement() {
      return element;
    }
  }

  public static class Size extends ListResponse {
    private int size;

    public Size(final int size) {
      super(ListOperation.SIZE);
      this.size = size;
    }

    public int getSize() {
      return size;
    }
  }


}
