/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.core.protocol.openwire;

import org.apache.activemq.api.core.SimpleString;
import org.apache.activemq.core.paging.impl.PagingStoreImpl;
import org.apache.activemq.core.settings.impl.AddressFullMessagePolicy;

/**
 * @author <a href="mailto:hgao@redhat.com">Howard Gao</a>
 */
public class SendingResult
{
   private boolean blockNextSend;
   private PagingStoreImpl blockPagingStore;
   private SimpleString blockingAddress;

   public void setBlockNextSend(boolean block)
   {
      this.blockNextSend = block;
   }

   public boolean isBlockNextSend()
   {
      return this.blockNextSend;
   }

   public void setBlockPagingStore(PagingStoreImpl store)
   {
      this.blockPagingStore = store;
   }

   public PagingStoreImpl getBlockPagingStore()
   {
      return this.blockPagingStore;
   }

   public void setBlockingAddress(SimpleString address)
   {
      this.blockingAddress = address;
   }

   public SimpleString getBlockingAddress()
   {
      return this.blockingAddress;
   }

   public boolean isSendFailIfNoSpace()
   {
      AddressFullMessagePolicy policy = this.blockPagingStore.getAddressFullMessagePolicy();
      return policy == AddressFullMessagePolicy.FAIL;
   }
}