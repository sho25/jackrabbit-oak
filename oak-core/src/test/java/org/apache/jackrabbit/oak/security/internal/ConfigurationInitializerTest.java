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
name|internal
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
name|plugins
operator|.
name|tree
operator|.
name|RootProvider
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
name|plugins
operator|.
name|tree
operator|.
name|TreeProvider
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
name|CompositeConfiguration
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
name|ConfigurationParameters
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
name|SecurityConfiguration
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

begin_class
specifier|public
class|class
name|ConfigurationInitializerTest
block|{
specifier|private
specifier|final
name|SecurityProvider
name|sp
init|=
operator|new
name|InternalSecurityProvider
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ConfigurationParameters
name|params
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
literal|"key"
argument_list|,
literal|"value"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|RootProvider
name|rootProvider
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|RootProvider
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|TreeProvider
name|treeProvider
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|TreeProvider
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testInitConfigurationReturnsSame
parameter_list|()
block|{
name|SecurityConfiguration
name|sc
init|=
operator|new
name|SecurityConfiguration
operator|.
name|Default
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|sc
argument_list|,
name|ConfigurationInitializer
operator|.
name|initializeConfiguration
argument_list|(
name|sc
argument_list|,
name|sp
argument_list|,
name|rootProvider
argument_list|,
name|treeProvider
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInitBaseConfigurationReturnsSame
parameter_list|()
block|{
name|SecurityConfiguration
name|sc
init|=
operator|new
name|TestConfiguration
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|sc
argument_list|,
name|ConfigurationInitializer
operator|.
name|initializeConfiguration
argument_list|(
name|sc
argument_list|,
name|sp
argument_list|,
name|rootProvider
argument_list|,
name|treeProvider
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInitConfigurationWithParamReturnsSame
parameter_list|()
block|{
name|SecurityConfiguration
name|sc
init|=
operator|new
name|SecurityConfiguration
operator|.
name|Default
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|sc
argument_list|,
name|ConfigurationInitializer
operator|.
name|initializeConfiguration
argument_list|(
name|sc
argument_list|,
name|sp
argument_list|,
name|params
argument_list|,
name|rootProvider
argument_list|,
name|treeProvider
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInitBaseConfigurationWithParamReturnsSame
parameter_list|()
block|{
name|SecurityConfiguration
name|sc
init|=
operator|new
name|TestConfiguration
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|sc
argument_list|,
name|ConfigurationInitializer
operator|.
name|initializeConfiguration
argument_list|(
name|sc
argument_list|,
name|sp
argument_list|,
name|params
argument_list|,
name|rootProvider
argument_list|,
name|treeProvider
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInitNonBaseConfiguration
parameter_list|()
block|{
name|SecurityConfiguration
name|sc
init|=
operator|new
name|SecurityConfiguration
operator|.
name|Default
argument_list|()
decl_stmt|;
name|ConfigurationInitializer
operator|.
name|initializeConfiguration
argument_list|(
name|sc
argument_list|,
name|sp
argument_list|,
name|rootProvider
argument_list|,
name|treeProvider
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|sc
operator|.
name|getParameters
argument_list|()
operator|.
name|containsKey
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInitBaseConfiguration
parameter_list|()
block|{
name|TestConfiguration
name|sc
init|=
operator|new
name|TestConfiguration
argument_list|()
decl_stmt|;
name|SecurityConfiguration
name|afterInit
init|=
name|ConfigurationInitializer
operator|.
name|initializeConfiguration
argument_list|(
name|sc
argument_list|,
name|sp
argument_list|,
name|rootProvider
argument_list|,
name|treeProvider
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|sc
argument_list|,
name|afterInit
argument_list|)
expr_stmt|;
comment|// verify securityprovider
name|assertSame
argument_list|(
name|sp
argument_list|,
name|sc
operator|.
name|getSecurityProvider
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify params
name|ConfigurationParameters
name|parameters
init|=
name|afterInit
operator|.
name|getParameters
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|parameters
operator|.
name|containsKey
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|parameters
operator|.
name|containsKey
argument_list|(
literal|"key2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"initialValue"
argument_list|,
name|parameters
operator|.
name|get
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"initialValue"
argument_list|,
name|parameters
operator|.
name|get
argument_list|(
literal|"key2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInitBaseConfigurationWithParam
parameter_list|()
block|{
name|TestConfiguration
name|sc
init|=
operator|new
name|TestConfiguration
argument_list|()
decl_stmt|;
name|SecurityConfiguration
name|afterInit
init|=
name|ConfigurationInitializer
operator|.
name|initializeConfiguration
argument_list|(
name|sc
argument_list|,
name|sp
argument_list|,
name|params
argument_list|,
name|rootProvider
argument_list|,
name|treeProvider
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|sc
argument_list|,
name|afterInit
argument_list|)
expr_stmt|;
comment|// verify securityprovider
name|assertSame
argument_list|(
name|sp
argument_list|,
name|sc
operator|.
name|getSecurityProvider
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify tree/root provider
name|assertSame
argument_list|(
name|rootProvider
argument_list|,
name|sc
operator|.
name|getRootProvider
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|treeProvider
argument_list|,
name|sc
operator|.
name|getTreeProvider
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify params
name|ConfigurationParameters
name|parameters
init|=
name|afterInit
operator|.
name|getParameters
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|parameters
operator|.
name|containsKey
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|parameters
operator|.
name|containsKey
argument_list|(
literal|"key2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value"
argument_list|,
name|parameters
operator|.
name|get
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"initialValue"
argument_list|,
name|parameters
operator|.
name|get
argument_list|(
literal|"key2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInitCompositeConfiguration
parameter_list|()
block|{
name|TestComposite
argument_list|<
name|SecurityConfiguration
operator|.
name|Default
argument_list|>
name|composite
init|=
operator|new
name|TestComposite
argument_list|<
name|SecurityConfiguration
operator|.
name|Default
argument_list|>
argument_list|()
decl_stmt|;
name|composite
operator|.
name|addConfiguration
argument_list|(
operator|new
name|SecurityConfiguration
operator|.
name|Default
argument_list|()
argument_list|)
expr_stmt|;
name|composite
operator|.
name|addConfiguration
argument_list|(
operator|new
name|SecurityConfiguration
operator|.
name|Default
argument_list|()
argument_list|)
expr_stmt|;
name|ConfigurationInitializer
operator|.
name|initializeConfigurations
argument_list|(
name|composite
argument_list|,
name|sp
argument_list|,
name|params
argument_list|,
name|rootProvider
argument_list|,
name|treeProvider
argument_list|)
expr_stmt|;
comment|// verify securityprovider
name|assertSame
argument_list|(
name|sp
argument_list|,
name|composite
operator|.
name|getSecurityProvider
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify params
for|for
control|(
name|SecurityConfiguration
operator|.
name|Default
name|sc
range|:
name|composite
operator|.
name|getConfigurations
argument_list|()
control|)
block|{
name|assertFalse
argument_list|(
name|sc
operator|.
name|getParameters
argument_list|()
operator|.
name|containsKey
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInitCompositeBaseConfiguration
parameter_list|()
block|{
name|TestComposite
argument_list|<
name|TestConfiguration
argument_list|>
name|composite
init|=
operator|new
name|TestComposite
argument_list|<
name|TestConfiguration
argument_list|>
argument_list|()
decl_stmt|;
name|composite
operator|.
name|addConfiguration
argument_list|(
operator|new
name|TestConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|composite
operator|.
name|addConfiguration
argument_list|(
operator|new
name|TestConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|ConfigurationInitializer
operator|.
name|initializeConfigurations
argument_list|(
name|composite
argument_list|,
name|sp
argument_list|,
name|params
argument_list|,
name|rootProvider
argument_list|,
name|treeProvider
argument_list|)
expr_stmt|;
comment|// verify securityprovider
name|assertSame
argument_list|(
name|sp
argument_list|,
name|composite
operator|.
name|getSecurityProvider
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify params
for|for
control|(
name|SecurityConfiguration
name|sc
range|:
name|composite
operator|.
name|getConfigurations
argument_list|()
control|)
block|{
name|ConfigurationParameters
name|parameters
init|=
name|sc
operator|.
name|getParameters
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|parameters
operator|.
name|containsKey
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|parameters
operator|.
name|containsKey
argument_list|(
literal|"key2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value"
argument_list|,
name|parameters
operator|.
name|get
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"initialValue"
argument_list|,
name|parameters
operator|.
name|get
argument_list|(
literal|"key2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|final
class|class
name|TestConfiguration
extends|extends
name|ConfigurationBase
block|{
name|TestConfiguration
parameter_list|()
block|{
name|super
argument_list|(
name|sp
argument_list|,
name|ConfigurationParameters
operator|.
name|of
argument_list|(
literal|"key"
argument_list|,
literal|"initialValue"
argument_list|,
literal|"key2"
argument_list|,
literal|"initialValue"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|final
class|class
name|TestComposite
parameter_list|<
name|T
extends|extends
name|SecurityConfiguration
parameter_list|>
extends|extends
name|CompositeConfiguration
argument_list|<
name|T
argument_list|>
block|{
specifier|public
name|TestComposite
parameter_list|()
block|{
name|super
argument_list|(
literal|"name"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|SecurityProvider
name|getSecurityProvider
parameter_list|()
block|{
return|return
name|super
operator|.
name|getSecurityProvider
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

