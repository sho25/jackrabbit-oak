begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|segment
operator|.
name|standby
operator|.
name|server
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketAddress
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|embedded
operator|.
name|EmbeddedChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|ClientFilterHandlerTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|allowedClientsShouldBeServed
parameter_list|()
block|{
name|EmbeddedChannel
name|channel
init|=
operator|new
name|EmbeddedChannel
argument_list|(
operator|new
name|ClientFilterHandler
argument_list|(
operator|new
name|ClientFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isAllowed
parameter_list|(
name|SocketAddress
name|address
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
argument_list|)
argument_list|)
decl_stmt|;
name|channel
operator|.
name|connect
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|"127.0.0.1"
argument_list|,
literal|8080
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|channel
operator|.
name|isOpen
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|disallowedClientsShouldNotBeServed
parameter_list|()
block|{
name|EmbeddedChannel
name|channel
init|=
operator|new
name|EmbeddedChannel
argument_list|(
operator|new
name|ClientFilterHandler
argument_list|(
operator|new
name|ClientFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isAllowed
parameter_list|(
name|SocketAddress
name|address
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
argument_list|)
argument_list|)
decl_stmt|;
name|channel
operator|.
name|connect
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|"127.0.0.2"
argument_list|,
literal|8080
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|channel
operator|.
name|isOpen
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

