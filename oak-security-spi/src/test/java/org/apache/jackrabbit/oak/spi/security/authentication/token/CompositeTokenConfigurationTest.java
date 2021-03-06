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
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|token
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|api
operator|.
name|Root
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
name|AbstractCompositeConfigurationTest
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
name|ConfigurationBase
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|CompositeTokenConfigurationTest
extends|extends
name|AbstractCompositeConfigurationTest
argument_list|<
name|TokenConfiguration
argument_list|>
block|{
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
block|{
name|compositeConfiguration
operator|=
operator|new
name|CompositeTokenConfiguration
argument_list|(
name|createSecurityProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|TokenConfiguration
name|createTokenConfiguration
parameter_list|()
block|{
return|return
operator|new
name|TestTokenConfig
argument_list|()
return|;
block|}
specifier|private
name|SecurityProvider
name|createSecurityProvider
parameter_list|()
block|{
return|return
name|Mockito
operator|.
name|mock
argument_list|(
name|SecurityProvider
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmptyConstructor
parameter_list|()
block|{
name|TokenConfiguration
name|composite
init|=
operator|new
name|CompositeTokenConfiguration
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|TokenConfiguration
operator|.
name|NAME
argument_list|,
name|composite
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmpty
parameter_list|()
block|{
name|List
argument_list|<
name|TokenConfiguration
argument_list|>
name|configs
init|=
name|getConfigurations
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|configs
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|configs
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetDefault
parameter_list|()
block|{
name|TokenConfiguration
name|tc
init|=
name|createTokenConfiguration
argument_list|()
decl_stmt|;
name|setDefault
argument_list|(
name|tc
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|TokenConfiguration
argument_list|>
name|configs
init|=
name|getConfigurations
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|configs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|configs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|addConfiguration
argument_list|(
name|tc
argument_list|)
expr_stmt|;
name|configs
operator|=
name|getConfigurations
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|configs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|configs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|addConfiguration
argument_list|(
name|createTokenConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|configs
operator|=
name|getConfigurations
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|configs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|configs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddConfiguration
parameter_list|()
block|{
name|TokenConfiguration
name|tc
init|=
name|createTokenConfiguration
argument_list|()
decl_stmt|;
name|addConfiguration
argument_list|(
name|tc
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|TokenConfiguration
argument_list|>
name|configs
init|=
name|getConfigurations
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|configs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|configs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|addConfiguration
argument_list|(
name|tc
argument_list|)
expr_stmt|;
name|configs
operator|=
name|getConfigurations
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|configs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|configs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|addConfiguration
argument_list|(
name|createTokenConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|configs
operator|=
name|getConfigurations
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|configs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|configs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveConfiguration
parameter_list|()
block|{
name|TokenConfiguration
name|tc
init|=
name|createTokenConfiguration
argument_list|()
decl_stmt|;
name|addConfiguration
argument_list|(
name|tc
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|TokenConfiguration
argument_list|>
name|configs
init|=
name|getConfigurations
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|configs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|configs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|removeConfiguration
argument_list|(
name|tc
argument_list|)
expr_stmt|;
name|configs
operator|=
name|getConfigurations
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|configs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|configs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetTokenProvider
parameter_list|()
block|{
name|CompositeTokenConfiguration
name|ctc
init|=
operator|(
name|CompositeTokenConfiguration
operator|)
name|compositeConfiguration
decl_stmt|;
name|Root
name|root
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Root
operator|.
name|class
argument_list|)
decl_stmt|;
name|TokenProvider
name|tp
init|=
name|ctc
operator|.
name|getTokenProvider
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|tp
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tp
operator|instanceof
name|CompositeTokenProvider
argument_list|)
expr_stmt|;
name|TokenConfiguration
name|tc
init|=
name|createTokenConfiguration
argument_list|()
decl_stmt|;
name|setDefault
argument_list|(
name|tc
argument_list|)
expr_stmt|;
name|tp
operator|=
name|ctc
operator|.
name|getTokenProvider
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|tp
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tp
operator|instanceof
name|CompositeTokenProvider
argument_list|)
expr_stmt|;
name|addConfiguration
argument_list|(
name|tc
argument_list|)
expr_stmt|;
name|tp
operator|=
name|ctc
operator|.
name|getTokenProvider
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|tp
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tp
operator|instanceof
name|CompositeTokenProvider
argument_list|)
expr_stmt|;
name|addConfiguration
argument_list|(
name|createTokenConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|tp
operator|=
name|ctc
operator|.
name|getTokenProvider
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|tp
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tp
operator|instanceof
name|CompositeTokenProvider
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
class|class
name|TestTokenConfig
extends|extends
name|ConfigurationBase
implements|implements
name|TokenConfiguration
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|TokenProvider
name|getTokenProvider
parameter_list|(
annotation|@
name|NotNull
name|Root
name|root
parameter_list|)
block|{
return|return
name|Mockito
operator|.
name|mock
argument_list|(
name|TokenProvider
operator|.
name|class
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

