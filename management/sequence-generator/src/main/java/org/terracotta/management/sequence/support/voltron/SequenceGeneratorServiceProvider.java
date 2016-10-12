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
package org.terracotta.management.sequence.support.voltron;

import com.tc.classloader.BuiltinService;
import org.terracotta.entity.ServiceConfiguration;
import org.terracotta.entity.ServiceProvider;
import org.terracotta.entity.ServiceProviderCleanupException;
import org.terracotta.entity.ServiceProviderConfiguration;
import org.terracotta.management.sequence.BoundaryFlakeSequenceGenerator;
import org.terracotta.management.sequence.NodeIdSource;
import org.terracotta.management.sequence.SequenceGenerator;
import org.terracotta.management.sequence.TimeSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Mathieu Carbou
 */
@BuiltinService
public class SequenceGeneratorServiceProvider implements ServiceProvider {

  private static final ArrayList<Class<?>> SERVICE_TYPES = new ArrayList<Class<?>>(Collections.singletonList(SequenceGenerator.class));

  private final SequenceGenerator sequenceGenerator = new BoundaryFlakeSequenceGenerator(TimeSource.BEST, NodeIdSource.BEST);

  @Override
  public void clear() throws ServiceProviderCleanupException {
  }

  @Override
  public boolean initialize(ServiceProviderConfiguration configuration) {
    // @BuiltinService cannot be initialized
    return true;
  }

  @Override
  public <T> T getService(long consumerID, ServiceConfiguration<T> configuration) {
    Class<T> serviceType = configuration.getServiceType();

    if (SequenceGenerator.class == serviceType) {
      return serviceType.cast(sequenceGenerator);
    }

    throw new IllegalStateException("Unknown service type " + serviceType.getName());
  }

  @Override
  public Collection<Class<?>> getProvidedServiceTypes() {
    return SERVICE_TYPES;
  }

}