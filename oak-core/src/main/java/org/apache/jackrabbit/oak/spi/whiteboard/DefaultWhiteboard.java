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
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_class
specifier|public
class|class
name|DefaultWhiteboard
implements|implements
name|Whiteboard
block|{
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Registration
name|register
parameter_list|(
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
specifier|final
name|T
name|service
parameter_list|,
specifier|final
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|properties
parameter_list|)
block|{
name|registered
argument_list|(
name|type
argument_list|,
name|service
argument_list|,
name|properties
argument_list|)
expr_stmt|;
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
name|unregistered
argument_list|(
name|type
argument_list|,
name|service
argument_list|,
name|properties
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
comment|//---------------------------------------------------------< protected>--
specifier|protected
name|void
name|registered
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|type
parameter_list|,
name|Object
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
block|{     }
specifier|protected
name|void
name|unregistered
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|type
parameter_list|,
name|Object
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
block|{     }
block|}
end_class

end_unit

