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
package|;
end_package

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
name|PropertyState
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
name|Tree
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
name|TreeLocation
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
name|assertNotSame
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

begin_class
specifier|public
class|class
name|CompositeContextTest
extends|extends
name|AbstractCompositeConfigurationTest
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
name|CompositeConfiguration
argument_list|(
literal|"test"
argument_list|,
name|Mockito
operator|.
name|mock
argument_list|(
name|SecurityProvider
operator|.
name|class
argument_list|)
argument_list|)
block|{}
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetContext
parameter_list|()
throws|throws
name|Exception
block|{
name|Class
name|cls
init|=
name|Class
operator|.
name|forName
argument_list|(
name|CompositeConfiguration
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"$CompositeContext"
argument_list|)
decl_stmt|;
name|Field
name|def
init|=
name|cls
operator|.
name|getDeclaredField
argument_list|(
literal|"defaultCtx"
argument_list|)
decl_stmt|;
name|def
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Field
name|delegatees
init|=
name|cls
operator|.
name|getDeclaredField
argument_list|(
literal|"delegatees"
argument_list|)
decl_stmt|;
name|delegatees
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Context
name|ctx
init|=
name|compositeConfiguration
operator|.
name|getContext
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|cls
argument_list|,
name|ctx
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|delegatees
operator|.
name|get
argument_list|(
name|ctx
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|Context
operator|.
name|DEFAULT
argument_list|,
name|def
operator|.
name|get
argument_list|(
name|ctx
argument_list|)
argument_list|)
expr_stmt|;
name|SecurityConfiguration
name|sc
init|=
operator|new
name|TestConfiguration
argument_list|()
decl_stmt|;
name|setDefault
argument_list|(
name|sc
argument_list|)
expr_stmt|;
name|ctx
operator|=
name|compositeConfiguration
operator|.
name|getContext
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|delegatees
operator|.
name|get
argument_list|(
name|ctx
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|sc
operator|.
name|getContext
argument_list|()
argument_list|,
name|def
operator|.
name|get
argument_list|(
name|ctx
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|cls
argument_list|,
name|ctx
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|addConfiguration
argument_list|(
name|sc
argument_list|)
expr_stmt|;
name|ctx
operator|=
name|compositeConfiguration
operator|.
name|getContext
argument_list|()
expr_stmt|;
name|assertNotSame
argument_list|(
name|sc
operator|.
name|getContext
argument_list|()
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
operator|(
operator|(
name|Context
index|[]
operator|)
name|delegatees
operator|.
name|get
argument_list|(
name|ctx
argument_list|)
operator|)
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// add configuration that has DEFAULT ctx -> must not be added
name|SecurityConfiguration
name|defConfig
init|=
operator|new
name|SecurityConfiguration
operator|.
name|Default
argument_list|()
decl_stmt|;
name|addConfiguration
argument_list|(
name|defConfig
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
operator|(
operator|(
name|Context
index|[]
operator|)
name|delegatees
operator|.
name|get
argument_list|(
name|compositeConfiguration
operator|.
name|getContext
argument_list|()
argument_list|)
operator|)
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// add same test configuration again -> no duplicate entries
name|addConfiguration
argument_list|(
name|sc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
operator|(
operator|(
name|Context
index|[]
operator|)
name|delegatees
operator|.
name|get
argument_list|(
name|compositeConfiguration
operator|.
name|getContext
argument_list|()
argument_list|)
operator|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|SecurityConfiguration
name|sc2
init|=
operator|new
name|TestConfiguration
argument_list|()
decl_stmt|;
name|addConfiguration
argument_list|(
name|sc2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
operator|(
operator|(
name|Context
index|[]
operator|)
name|delegatees
operator|.
name|get
argument_list|(
name|compositeConfiguration
operator|.
name|getContext
argument_list|()
argument_list|)
operator|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|removeConfiguration
argument_list|(
name|sc2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
operator|(
operator|(
name|Context
index|[]
operator|)
name|delegatees
operator|.
name|get
argument_list|(
name|compositeConfiguration
operator|.
name|getContext
argument_list|()
argument_list|)
operator|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|removeConfiguration
argument_list|(
name|sc
argument_list|)
expr_stmt|;
name|removeConfiguration
argument_list|(
name|sc
argument_list|)
expr_stmt|;
name|removeConfiguration
argument_list|(
name|defConfig
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|delegatees
operator|.
name|get
argument_list|(
name|compositeConfiguration
operator|.
name|getContext
argument_list|()
argument_list|)
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
name|Context
name|ctx
init|=
name|compositeConfiguration
operator|.
name|getContext
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|Tree
name|tree
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|ctx
operator|.
name|definesContextRoot
argument_list|(
name|tree
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ctx
operator|.
name|definesInternal
argument_list|(
name|tree
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ctx
operator|.
name|definesTree
argument_list|(
name|tree
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ctx
operator|.
name|definesProperty
argument_list|(
name|tree
argument_list|,
name|Mockito
operator|.
name|mock
argument_list|(
name|PropertyState
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ctx
operator|.
name|definesLocation
argument_list|(
name|TreeLocation
operator|.
name|create
argument_list|(
name|tree
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesProperty
parameter_list|()
block|{
name|TestConfiguration
name|testConfig
init|=
operator|new
name|TestConfiguration
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|addConfiguration
argument_list|(
name|testConfig
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|compositeConfiguration
operator|.
name|getContext
argument_list|()
operator|.
name|definesProperty
argument_list|(
name|Mockito
operator|.
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
argument_list|,
name|Mockito
operator|.
name|mock
argument_list|(
name|PropertyState
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"definesProperty"
argument_list|,
name|testConfig
operator|.
name|ctx
operator|.
name|method
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesProperty2
parameter_list|()
block|{
name|TestConfiguration
name|testConfig
init|=
operator|new
name|TestConfiguration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|addConfiguration
argument_list|(
name|testConfig
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|compositeConfiguration
operator|.
name|getContext
argument_list|()
operator|.
name|definesProperty
argument_list|(
name|Mockito
operator|.
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
argument_list|,
name|Mockito
operator|.
name|mock
argument_list|(
name|PropertyState
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"definesProperty"
argument_list|,
name|testConfig
operator|.
name|ctx
operator|.
name|method
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesContextRoot
parameter_list|()
block|{
name|TestConfiguration
name|testConfig
init|=
operator|new
name|TestConfiguration
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|addConfiguration
argument_list|(
name|testConfig
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|compositeConfiguration
operator|.
name|getContext
argument_list|()
operator|.
name|definesContextRoot
argument_list|(
name|Mockito
operator|.
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"definesContextRoot"
argument_list|,
name|testConfig
operator|.
name|ctx
operator|.
name|method
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesContextRoot2
parameter_list|()
block|{
name|TestConfiguration
name|testConfig
init|=
operator|new
name|TestConfiguration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|addConfiguration
argument_list|(
name|testConfig
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|compositeConfiguration
operator|.
name|getContext
argument_list|()
operator|.
name|definesContextRoot
argument_list|(
name|Mockito
operator|.
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"definesContextRoot"
argument_list|,
name|testConfig
operator|.
name|ctx
operator|.
name|method
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesTree
parameter_list|()
block|{
name|TestConfiguration
name|testConfig
init|=
operator|new
name|TestConfiguration
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|addConfiguration
argument_list|(
name|testConfig
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|compositeConfiguration
operator|.
name|getContext
argument_list|()
operator|.
name|definesTree
argument_list|(
name|Mockito
operator|.
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"definesTree"
argument_list|,
name|testConfig
operator|.
name|ctx
operator|.
name|method
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesTree2
parameter_list|()
block|{
name|TestConfiguration
name|testConfig
init|=
operator|new
name|TestConfiguration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|addConfiguration
argument_list|(
name|testConfig
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|compositeConfiguration
operator|.
name|getContext
argument_list|()
operator|.
name|definesTree
argument_list|(
name|Mockito
operator|.
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"definesTree"
argument_list|,
name|testConfig
operator|.
name|ctx
operator|.
name|method
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesLocation
parameter_list|()
block|{
name|TestConfiguration
name|testConfig
init|=
operator|new
name|TestConfiguration
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|addConfiguration
argument_list|(
name|testConfig
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|compositeConfiguration
operator|.
name|getContext
argument_list|()
operator|.
name|definesLocation
argument_list|(
name|TreeLocation
operator|.
name|create
argument_list|(
name|Mockito
operator|.
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"definesLocation"
argument_list|,
name|testConfig
operator|.
name|ctx
operator|.
name|method
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesLocation2
parameter_list|()
block|{
name|TestConfiguration
name|testConfig
init|=
operator|new
name|TestConfiguration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|addConfiguration
argument_list|(
name|testConfig
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|compositeConfiguration
operator|.
name|getContext
argument_list|()
operator|.
name|definesLocation
argument_list|(
name|TreeLocation
operator|.
name|create
argument_list|(
name|Mockito
operator|.
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"definesLocation"
argument_list|,
name|testConfig
operator|.
name|ctx
operator|.
name|method
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesInternal
parameter_list|()
block|{
name|TestConfiguration
name|testConfig
init|=
operator|new
name|TestConfiguration
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|addConfiguration
argument_list|(
name|testConfig
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|compositeConfiguration
operator|.
name|getContext
argument_list|()
operator|.
name|definesInternal
argument_list|(
name|Mockito
operator|.
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"definesInternal"
argument_list|,
name|testConfig
operator|.
name|ctx
operator|.
name|method
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesInternal2
parameter_list|()
block|{
name|TestConfiguration
name|testConfig
init|=
operator|new
name|TestConfiguration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|addConfiguration
argument_list|(
name|testConfig
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|compositeConfiguration
operator|.
name|getContext
argument_list|()
operator|.
name|definesInternal
argument_list|(
name|Mockito
operator|.
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"definesInternal"
argument_list|,
name|testConfig
operator|.
name|ctx
operator|.
name|method
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
class|class
name|TestConfiguration
extends|extends
name|SecurityConfiguration
operator|.
name|Default
block|{
specifier|private
specifier|final
name|TestContext
name|ctx
decl_stmt|;
specifier|private
name|TestConfiguration
parameter_list|()
block|{
name|this
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|TestConfiguration
parameter_list|(
name|boolean
name|returnValue
parameter_list|)
block|{
name|this
operator|.
name|ctx
operator|=
operator|new
name|TestContext
argument_list|(
name|returnValue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Context
name|getContext
parameter_list|()
block|{
return|return
name|ctx
return|;
block|}
block|}
specifier|private
specifier|static
specifier|final
class|class
name|TestContext
extends|extends
name|Context
operator|.
name|Default
block|{
specifier|private
name|String
name|method
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|returnValue
decl_stmt|;
specifier|private
name|TestContext
parameter_list|(
name|boolean
name|returnValue
parameter_list|)
block|{
name|this
operator|.
name|returnValue
operator|=
name|returnValue
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|definesProperty
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|parent
parameter_list|,
annotation|@
name|NotNull
name|PropertyState
name|property
parameter_list|)
block|{
name|method
operator|=
literal|"definesProperty"
expr_stmt|;
return|return
name|returnValue
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|definesContextRoot
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|tree
parameter_list|)
block|{
name|method
operator|=
literal|"definesContextRoot"
expr_stmt|;
return|return
name|returnValue
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|definesTree
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|tree
parameter_list|)
block|{
name|method
operator|=
literal|"definesTree"
expr_stmt|;
return|return
name|returnValue
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|definesLocation
parameter_list|(
annotation|@
name|NotNull
name|TreeLocation
name|location
parameter_list|)
block|{
name|method
operator|=
literal|"definesLocation"
expr_stmt|;
return|return
name|returnValue
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|definesInternal
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|tree
parameter_list|)
block|{
name|method
operator|=
literal|"definesInternal"
expr_stmt|;
return|return
name|returnValue
return|;
block|}
block|}
block|}
end_class

end_unit

