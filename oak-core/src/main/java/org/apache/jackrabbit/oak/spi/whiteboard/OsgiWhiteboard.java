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
name|spi
operator|.
name|whiteboard
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Dictionary
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|BundleContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|ServiceRegistration
import|;
end_import

begin_comment
comment|/**  * OSGi-based whiteboard implementation.  */
end_comment

begin_class
specifier|public
class|class
name|OsgiWhiteboard
implements|implements
name|Whiteboard
block|{
specifier|private
specifier|final
name|BundleContext
name|context
decl_stmt|;
specifier|public
name|OsgiWhiteboard
parameter_list|(
annotation|@
name|Nonnull
name|BundleContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|checkNotNull
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Registration
name|register
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
name|T
name|service
parameter_list|,
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|properties
parameter_list|)
block|{
name|Dictionary
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|dictionary
init|=
operator|new
name|Hashtable
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|entry
range|:
name|properties
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|dictionary
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ServiceRegistration
name|registration
init|=
name|context
operator|.
name|registerService
argument_list|(
name|type
operator|.
name|getName
argument_list|()
argument_list|,
name|service
argument_list|,
name|dictionary
argument_list|)
decl_stmt|;
return|return
operator|new
name|Registration
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|unregister
parameter_list|()
block|{
name|registration
operator|.
name|unregister
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

