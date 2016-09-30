package org.terracotta.toolkit.list;

import org.terracotta.entity.ClientDescriptor;
import org.terracotta.entity.MessageCodecException;
import org.terracotta.toolkit.ServerHandler;
import org.terracotta.toolkit.ToolkitResponse;
import org.terracotta.toolkit.ToolkitResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListServerHandler extends ServerHandler {

  private ListCodec codec;
  private List store;

  public ListServerHandler(final String type, final String name, final ClientDescriptor creator) {
    super(type, name, creator);
    codec = new ListCodec();
    store = new ArrayList();
  }

  @Override
  public ToolkitResponse handleMessage(final ClientDescriptor creator, final byte[] raw) throws MessageCodecException {
    ListRequest request = codec.decodeMessage(raw);
    try {
      switch (request.getOpCode()) {
        case ADD: {
          byte[] element = ((ListRequest.AddMessage)request).getElement();
          boolean flag = store.add(codec.deserializeElement(element));
          return wrap(true, new ListResponse.Add(toInt(flag)));
        }
        case ADD_AT: {
          byte[] element = ((ListRequest.AddAtMessage)request).getElement();
          int index = ((ListRequest.AddAtMessage)request).getIndex();
          store.add(index, codec.deserializeElement(element));
          return wrap(true, new ListResponse.AddAt());
        }
        case REMOVE: {
          byte[] element = ((ListRequest.RemoveMessage)request).getElement();
          boolean flag = store.remove(codec.deserializeElement(element));
          return wrap(true, new ListResponse.Remove(toInt(flag)));
        }
        case REMOVE_AT: {
          int index = ((ListRequest.RemoveAtMessage)request).getIndex();
          Object element = store.remove(index);
          if (element != null) {
            return wrap(true, new ListResponse.RemoveAt(codec.serializeElement(element)));
          } else {
            return wrap(true, new ListResponse.RemoveAt(null));
          }
        }
        case GET: {
          int index = ((ListRequest.GetMessage)request).getIndex();
          Object element = store.get(index);
          if (element != null) {
            return wrap(true, new ListResponse.Get(codec.serializeElement(element)));
          } else {
            return wrap(true, new ListResponse.Get(null));
          }
        }
        case SIZE: {
          return wrap(true, new ListResponse.Size(store.size()));
        }
        default:
          throw new RuntimeException("Not supported");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void handleReconnect(final ClientDescriptor creator, final byte[] raw) throws MessageCodecException {

  }

  private ToolkitResponse wrap(boolean success, ListResponse response) {
    return new ToolkitResponse() {
      @Override
      public ToolkitResult result() {
        return success ? ToolkitResult.SUCCESS : ToolkitResult.FAIL;
      }

      @Override
      public byte[] payload() {
        try {
          return codec.encodeResponse(response);
        } catch (MessageCodecException m) {
          throw new RuntimeException(m);
        }
      }

      @Override
      public String type() {
        return getType();
      }

      @Override
      public String name() {
        return getName();
      }


    };
  }

  private int toInt(boolean b) {
    if (b)
      return 1;
    else
      return 0;
  }
}
