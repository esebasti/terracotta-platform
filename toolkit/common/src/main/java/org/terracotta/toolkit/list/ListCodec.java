package org.terracotta.toolkit.list;

import org.terracotta.entity.MessageCodec;
import org.terracotta.entity.MessageCodecException;
import org.terracotta.runnel.EnumMapping;
import org.terracotta.runnel.EnumMappingBuilder;
import org.terracotta.runnel.Struct;
import org.terracotta.runnel.StructBuilder;
import org.terracotta.runnel.decoding.StructDecoder;
import org.terracotta.runnel.encoding.StructEncoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import static org.terracotta.toolkit.list.ListOperation.ADD_AT;
import static org.terracotta.toolkit.list.ListOperation.REMOVE_AT;

public class ListCodec implements MessageCodec<ListRequest, ListResponse> {


  private static final EnumMapping LIST_CMDS = EnumMappingBuilder.newEnumMappingBuilder(ListOperation.class)
      .mapping(ListOperation.ADD, 1)
      .mapping(ADD_AT, 2)
      .mapping(ListOperation.REMOVE, 3)
      .mapping(REMOVE_AT, 4)
      .mapping(ListOperation.SET, 5)
      .mapping(ListOperation.CLEAR, 6)
      .mapping(ListOperation.GET, 7)
      .mapping(ListOperation.SIZE, 8)
      .mapping(ListOperation.IS_EMPTY, 9)
      .mapping(ListOperation.CONTAINS, 10)
      .mapping(ListOperation.INDEX_OF, 11)
      .build();

  private static final Struct LIST_REQUEST_STRUCT = StructBuilder.newStructBuilder()
      .enm("opCode", 1, LIST_CMDS)
      .int32("index", 2)
      .byteBuffer("element", 3)
      .build();

  private static final Struct LIST_RESPONSE_STRUCT = StructBuilder.newStructBuilder()
      .enm("opCode", 1, LIST_CMDS)
      .int32("index", 2)
      .byteBuffer("element", 3)
      .int32("flag", 4)
      .int32("size", 5)
      .build();

  @Override
  public byte[] encodeMessage(final ListRequest message) throws MessageCodecException {
    StructEncoder encoder = LIST_REQUEST_STRUCT.encoder();
    encoder.enm("opCode", message.getOpCode());
    switch (message.getOpCode()) {
      case ADD: {
        ListRequest.AddMessage addMessage = (ListRequest.AddMessage)message;
        encoder.byteBuffer("element", ByteBuffer.wrap(addMessage.getElement()));
        break;
      }
      case ADD_AT: {
        ListRequest.AddAtMessage addAtMessage = (ListRequest.AddAtMessage)message;
        encoder.int32("index", addAtMessage.getIndex())
            .byteBuffer("element", ByteBuffer.wrap(addAtMessage.getElement()));
        break;
      }
      case REMOVE: {
        ListRequest.RemoveMessage removeMessage = (ListRequest.RemoveMessage)message;
        encoder.byteBuffer("element", ByteBuffer.wrap(removeMessage.getElement()));
        break;
      }
      case REMOVE_AT: {
        ListRequest.RemoveAtMessage removeAtMessage = (ListRequest.RemoveAtMessage)message;
        encoder.int32("index", removeAtMessage.getIndex());
        break;
      }
      case GET: {
        encoder.int32("index", ((ListRequest.GetMessage)message).getIndex());
        break;
      }
      case SIZE:
        break;
      default:
        throw new RuntimeException("Not supported");
    }
    ByteBuffer byteBuffer = encoder.encode();
    byte[] data = new byte[byteBuffer.flip().remaining()];
    byteBuffer.get(data);
    return data;
  }

  @Override
  public ListRequest decodeMessage(final byte[] payload) throws MessageCodecException {
    StructDecoder decoder = LIST_REQUEST_STRUCT.decoder(ByteBuffer.wrap(payload));
    ListOperation cmd = decoder.enm("opCode");
    switch (cmd) {
      case ADD: {
        ByteBuffer byteBuffer = decoder.byteBuffer("element");
        byte[] element = new byte[byteBuffer.remaining()];
        byteBuffer.get(element);
        return new ListRequest.AddMessage(element);
      }
      case ADD_AT: {
        int index = decoder.int32("index");
        ByteBuffer byteBuffer = decoder.byteBuffer("element");
        byte[] element = new byte[byteBuffer.remaining()];
        byteBuffer.get(element);
        return new ListRequest.AddAtMessage(element, index);
      }
      case REMOVE: {
        ByteBuffer byteBuffer = decoder.byteBuffer("element");
        byte[] element = new byte[byteBuffer.remaining()];
        byteBuffer.get(element);
        return new ListRequest.RemoveMessage(element);
      }
      case REMOVE_AT: {
        int index = decoder.int32("index");
        return new ListRequest.RemoveAtMessage(index);
      }
      case GET:
        return new ListRequest.GetMessage(decoder.int32("index"));
      case SIZE:
        return new ListRequest.SizeMessage();
      default:
        throw new RuntimeException("Not supported");
    }
  }

  @Override
  public byte[] encodeResponse(final ListResponse response) throws MessageCodecException {
    StructEncoder encoder = LIST_RESPONSE_STRUCT.encoder();
    encoder.enm("opCode", response.getOpCode());
    switch (response.getOpCode()) {
      case ADD: {
        ListResponse.Add addResponse = (ListResponse.Add)response;
        encoder.int32("flag", addResponse.getFlag());
        break;
      }
      case ADD_AT:
        break;
      case REMOVE: {
        ListResponse.Remove removeResponse = (ListResponse.Remove)response;
        encoder.int32("flag", removeResponse.getFlag());
        break;
      }
      case REMOVE_AT: {
        ListResponse.RemoveAt removeAtResponse = (ListResponse.RemoveAt)response;
        encoder.byteBuffer("element", ByteBuffer.wrap(removeAtResponse.getElement()));
        break;
      }
      case GET: {
        ListResponse.Get getResponse = (ListResponse.Get)response;
        if (getResponse.getElement() != null) {
          encoder.byteBuffer("element", ByteBuffer.wrap(getResponse.getElement()));
        }
        break;
      }
      case SIZE: {
        ListResponse.Size sizeResponse = (ListResponse.Size)response;
        encoder.int32("size", sizeResponse.getSize());
        break;
      }
      default:
        throw new RuntimeException("Not supported");
    }
    ByteBuffer byteBuffer = encoder.encode();
    byte[] data = new byte[byteBuffer.flip().remaining()];
    byteBuffer.get(data);
    return data;
  }

  @Override
  public ListResponse decodeResponse(final byte[] payload) throws MessageCodecException {
    StructDecoder decoder = LIST_RESPONSE_STRUCT.decoder(ByteBuffer.wrap(payload));
    ListOperation cmd = decoder.enm("opCode");
    switch (cmd) {
      case ADD:
        return new ListResponse.Add(decoder.int32("flag"));
      case ADD_AT:
        return new ListResponse.AddAt();
      case REMOVE:
        return new ListResponse.Remove(decoder.int32("flag"));
      case REMOVE_AT: {
        ByteBuffer byteBuffer = decoder.byteBuffer("element");
        if (byteBuffer != null) {
          byte[] element = new byte[byteBuffer.remaining()];
          byteBuffer.get(element);
          return new ListResponse.RemoveAt(element);
        } else {
          return new ListResponse.RemoveAt(null);
        }
      }
      case GET: {
        ByteBuffer byteBuffer = decoder.byteBuffer("element");
        if (byteBuffer != null) {
          byte[] element = new byte[byteBuffer.remaining()];
          byteBuffer.get(element);
          return new ListResponse.Get(element);
        } else {
          return new ListResponse.Get(null);
        }
      }
      case SIZE:
        return new ListResponse.Size(decoder.int32("size"));
      default:
        throw new RuntimeException("Not supported");
    }

  }


  public Object deserializeElement(byte[] raw) throws IOException, ClassNotFoundException {
    ByteArrayInputStream bis = new ByteArrayInputStream(raw);
    ObjectInputStream ois = new ObjectInputStream(bis);
    Object o = ois.readObject();
    ois.close();
    return o;
  }

  public byte[] serializeElement(Object o) throws IOException, ClassNotFoundException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(bos);
    oos.writeObject(o);
    oos.close();
    return bos.toByteArray();
  }
}
