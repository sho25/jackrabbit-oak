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
name|security
operator|.
name|authentication
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
name|api
operator|.
name|ContentRepository
import|;
end_import

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
name|security
operator|.
name|internal
operator|.
name|SecurityProviderBuilder
import|;
end_import

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
name|security
operator|.
name|SecurityProvider
import|;
end_import

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
name|security
operator|.
name|authentication
operator|.
name|AuthenticationConfiguration
import|;
end_import

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
name|security
operator|.
name|authentication
operator|.
name|LoginContextProvider
import|;
end_import

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
name|security
operator|.
name|authentication
operator|.
name|LoginModuleMonitor
import|;
end_import

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
name|whiteboard
operator|.
name|DefaultWhiteboard
import|;
end_import

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
name|whiteboard
operator|.
name|Whiteboard
import|;
end_import

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
name|whiteboard
operator|.
name|WhiteboardAware
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

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
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
name|assertEquals
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
name|assertNotNull
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
name|assertNull
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
name|assertSame
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
specifier|public
class|class
name|AuthenticationConfigurationImplTest
block|{
specifier|private
specifier|final
name|AuthenticationConfigurationImpl
name|authConfiguration
init|=
operator|new
name|AuthenticationConfigurationImpl
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ContentRepository
name|repo
init|=
name|mock
argument_list|(
name|ContentRepository
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testGetName
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|AuthenticationConfiguration
operator|.
name|NAME
argument_list|,
name|authConfiguration
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGetLoginCtxProviderNotInitialized
parameter_list|()
block|{
name|authConfiguration
operator|.
name|getLoginContextProvider
argument_list|(
name|repo
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetLoginCtxProvider
parameter_list|()
block|{
name|authConfiguration
operator|.
name|setSecurityProvider
argument_list|(
name|SecurityProviderBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|authConfiguration
operator|.
name|getLoginContextProvider
argument_list|(
name|repo
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetLoginCtxProviderWhiteboard
parameter_list|()
throws|throws
name|Exception
block|{
name|Whiteboard
name|wb
init|=
operator|new
name|DefaultWhiteboard
argument_list|()
decl_stmt|;
name|SecurityProvider
name|sp
init|=
name|mock
argument_list|(
name|SecurityProvider
operator|.
name|class
argument_list|,
name|Mockito
operator|.
name|withSettings
argument_list|()
operator|.
name|extraInterfaces
argument_list|(
name|WhiteboardAware
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|when
argument_list|(
operator|(
operator|(
name|WhiteboardAware
operator|)
name|sp
operator|)
operator|.
name|getWhiteboard
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|wb
argument_list|)
expr_stmt|;
name|authConfiguration
operator|.
name|setSecurityProvider
argument_list|(
name|sp
argument_list|)
expr_stmt|;
name|LoginContextProvider
name|lcp
init|=
name|authConfiguration
operator|.
name|getLoginContextProvider
argument_list|(
name|repo
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|lcp
operator|instanceof
name|LoginContextProviderImpl
argument_list|)
expr_stmt|;
name|Field
name|f
init|=
name|LoginContextProviderImpl
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"whiteboard"
argument_list|)
decl_stmt|;
name|f
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|wb
argument_list|,
name|f
operator|.
name|get
argument_list|(
name|lcp
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetLoginCtxProviderWithoutWhiteboard
parameter_list|()
throws|throws
name|Exception
block|{
name|SecurityProvider
name|sp
init|=
name|mock
argument_list|(
name|SecurityProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|authConfiguration
operator|.
name|setSecurityProvider
argument_list|(
name|sp
argument_list|)
expr_stmt|;
name|LoginContextProvider
name|lcp
init|=
name|authConfiguration
operator|.
name|getLoginContextProvider
argument_list|(
name|repo
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|lcp
operator|instanceof
name|LoginContextProviderImpl
argument_list|)
expr_stmt|;
name|Field
name|f
init|=
name|LoginContextProviderImpl
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"whiteboard"
argument_list|)
decl_stmt|;
name|f
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|f
operator|.
name|get
argument_list|(
name|lcp
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetLoginModuleMonitor
parameter_list|()
throws|throws
name|Exception
block|{
name|Field
name|f
init|=
name|AuthenticationConfigurationImpl
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"lmMonitor"
argument_list|)
decl_stmt|;
name|f
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|LoginModuleMonitor
operator|.
name|NOOP
argument_list|,
name|f
operator|.
name|get
argument_list|(
name|authConfiguration
argument_list|)
argument_list|)
expr_stmt|;
name|LoginModuleMonitor
name|monitor
init|=
name|mock
argument_list|(
name|LoginModuleMonitor
operator|.
name|class
argument_list|)
decl_stmt|;
name|authConfiguration
operator|.
name|setLoginModuleMonitor
argument_list|(
name|monitor
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|monitor
argument_list|,
name|f
operator|.
name|get
argument_list|(
name|authConfiguration
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

