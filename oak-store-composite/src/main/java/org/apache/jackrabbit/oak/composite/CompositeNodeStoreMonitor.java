begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|composite
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|mount
operator|.
name|Mount
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|annotation
operator|.
name|versioning
operator|.
name|ProviderType
import|;
end_import

begin_interface
annotation|@
name|ProviderType
specifier|public
interface|interface
name|CompositeNodeStoreMonitor
block|{
name|void
name|onCreateNodeObject
parameter_list|(
name|String
name|path
parameter_list|)
function_decl|;
name|void
name|onSwitchNodeToNative
parameter_list|(
name|Mount
name|mount
parameter_list|)
function_decl|;
name|void
name|onAddStringCacheEntry
parameter_list|()
function_decl|;
name|CompositeNodeStoreMonitor
name|EMPTY_INSTANCE
init|=
operator|new
name|CompositeNodeStoreMonitor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onCreateNodeObject
parameter_list|(
name|String
name|path
parameter_list|)
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|onSwitchNodeToNative
parameter_list|(
name|Mount
name|mount
parameter_list|)
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|onAddStringCacheEntry
parameter_list|()
block|{         }
block|}
decl_stmt|;
block|}
end_interface

end_unit

