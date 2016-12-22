/*
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terracotta.voltron.proxy.server;

import org.terracotta.entity.ActiveServerEntity;
import org.terracotta.entity.ClientDescriptor;
import org.terracotta.entity.PassiveSynchronizationChannel;
import org.terracotta.voltron.proxy.Codec;
import org.terracotta.voltron.proxy.ProxyEntityMessage;
import org.terracotta.voltron.proxy.ProxyEntityResponse;

import java.util.Objects;

/**
 * @author Alex Snaps
 */
public abstract class ActiveProxiedServerEntity<T, S, R, M> implements ActiveServerEntity<ProxyEntityMessage, ProxyEntityResponse> {

  private final T entity;
  private S synchronizer;
  private final M messenger;

  private final ProxyInvoker<T> entityInvoker;
  private final ProxyInvoker<M> messengerInvoker;

  private Codec codec;
  private Class<R> reconnectDataType;

  public ActiveProxiedServerEntity(T entity, M messenger) {
    this.entity = Objects.requireNonNull(entity);
    this.messenger = messenger; // can be null
    this.entityInvoker = new ProxyInvoker<>(entity);
    this.messengerInvoker = new ProxyInvoker<>(messenger);
  }

  @Override
  public final ProxyEntityResponse invoke(final ClientDescriptor clientDescriptor, final ProxyEntityMessage msg) {
    switch (msg.getType()) {
      case MESSAGE:
        return entityInvoker.invoke(msg, clientDescriptor);
      case MESSENGER:
        return messengerInvoker.invoke(msg, clientDescriptor);
      default:
        throw new AssertionError(msg.getType());
    }
  }

  @Override
  public void connected(ClientDescriptor clientDescriptor) {
    entityInvoker.addClient(clientDescriptor);
  }

  @Override
  public void disconnected(ClientDescriptor clientDescriptor) {
    entityInvoker.removeClient(clientDescriptor);
  }

  @Override
  public final void handleReconnect(final ClientDescriptor clientDescriptor, final byte[] extendedReconnectData) {
    if (reconnectDataType != null && codec != null) {
      R state = null;
      if (extendedReconnectData != null && extendedReconnectData.length > 0) {
        state = codec.decode(reconnectDataType, extendedReconnectData);

      }
      onReconnect(clientDescriptor, state);
    }
  }

  @Override
  public final void synchronizeKeyToPassive(final PassiveSynchronizationChannel<ProxyEntityMessage> channel, final int concurrencyKey) {
    if (synchronizer != null) {
      SyncProxyFactory.setCurrentChannel(channel);
      try {
        synchronizeKeyToPassive(concurrencyKey);
      } finally {
        SyncProxyFactory.removeCurrentChannel();
      }
    }
  }

  @Override
  public void createNew() {
    // Don't care I think
  }

  @Override
  public void loadExisting() {
    // Don't care I think
  }

  @Override
  public void destroy() {
    // Don't care I think
  }

  protected void synchronizeKeyToPassive(int concurrencyKey) {
  }

  protected void onReconnect(ClientDescriptor clientDescriptor, R state) {
  }

  protected final <M> void fireMessage(Class<M> type, M message, boolean echo) {entityInvoker.fireMessage(type, message, echo);}

  protected final <M> void fireMessage(Class<M> type, M message, ClientDescriptor[] clients) {entityInvoker.fireMessage(type, message, clients);}

  protected final T getEntity() {
    return entity;
  }

  protected final S getSynchronizer() {
    return synchronizer;
  }

  protected final M getMessenger() {
    return messenger;
  }

  final ProxyInvoker<T> getEntityInvoker() {
    return entityInvoker;
  }

  final void setSynchronizer(S synchronizer) {
    this.synchronizer = synchronizer;
  }

  final void setReconnect(Class<R> reconnectDataType, Codec codec) {
    this.reconnectDataType = reconnectDataType;
    this.codec = codec;
  }
}
