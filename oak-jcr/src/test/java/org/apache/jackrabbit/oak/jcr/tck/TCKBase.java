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
name|jcr
operator|.
name|tck
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Properties
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
name|jcr
operator|.
name|OakMongoMKRepositoryStub
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
name|jcr
operator|.
name|OakRepositoryStub
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
name|jcr
operator|.
name|OakSegmentMKRepositoryStub
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
name|test
operator|.
name|RepositoryHelper
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
name|test
operator|.
name|RepositoryHelperPool
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
name|test
operator|.
name|RepositoryHelperPoolImpl
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
name|test
operator|.
name|RepositoryStub
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|bridge
operator|.
name|SLF4JBridgeHandler
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestSuite
import|;
end_import

begin_comment
comment|/**  * Base class for TCK tests.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|TCKBase
extends|extends
name|TestSuite
block|{
static|static
block|{
name|SLF4JBridgeHandler
operator|.
name|removeHandlersForRootLogger
argument_list|()
expr_stmt|;
name|SLF4JBridgeHandler
operator|.
name|install
argument_list|()
expr_stmt|;
block|}
specifier|public
name|TCKBase
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|Setup
operator|.
name|wrap
argument_list|(
name|this
argument_list|,
name|OakRepositoryStub
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|OakSegmentMKRepositoryStub
operator|.
name|isAvailable
argument_list|()
condition|)
block|{
name|Setup
operator|.
name|wrap
argument_list|(
name|this
argument_list|,
name|OakSegmentMKRepositoryStub
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|OakMongoMKRepositoryStub
operator|.
name|isMongoDBAvailable
argument_list|()
condition|)
block|{
name|Setup
operator|.
name|wrap
argument_list|(
name|this
argument_list|,
name|OakMongoMKRepositoryStub
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|abstract
specifier|protected
name|void
name|addTests
parameter_list|()
function_decl|;
comment|/**      * Setup test class to replace the RepositoryHelper. This is quite a hack      * because the existing TCK tests do not take parameters.      */
specifier|public
specifier|static
class|class
name|Setup
extends|extends
name|TestCase
block|{
specifier|private
specifier|final
name|String
name|stubClass
decl_stmt|;
specifier|private
name|List
argument_list|<
name|RepositoryHelper
argument_list|>
name|helpers
init|=
operator|new
name|ArrayList
argument_list|<
name|RepositoryHelper
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
specifier|static
name|void
name|wrap
parameter_list|(
name|TCKBase
name|test
parameter_list|,
name|String
name|stubClass
parameter_list|)
block|{
name|Setup
name|setup
init|=
operator|new
name|Setup
argument_list|(
name|stubClass
argument_list|)
decl_stmt|;
name|test
operator|.
name|addTest
argument_list|(
name|setup
argument_list|)
expr_stmt|;
name|test
operator|.
name|addTests
argument_list|()
expr_stmt|;
name|test
operator|.
name|addTest
argument_list|(
name|setup
operator|.
name|getTearDown
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Setup
parameter_list|(
name|String
name|stubClass
parameter_list|)
block|{
name|super
argument_list|(
literal|"testSetup"
argument_list|)
expr_stmt|;
name|this
operator|.
name|stubClass
operator|=
name|stubClass
expr_stmt|;
block|}
specifier|public
name|void
name|testSetup
parameter_list|()
throws|throws
name|Exception
block|{
comment|// replace the existing helper with our parametrized version
name|RepositoryHelperPool
name|helperPool
init|=
name|RepositoryHelperPoolImpl
operator|.
name|getInstance
argument_list|()
decl_stmt|;
comment|// drain helpers
name|helpers
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|helperPool
operator|.
name|borrowHelpers
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// replace with our own stub
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|load
argument_list|(
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|RepositoryStub
operator|.
name|STUB_IMPL_PROPS
argument_list|)
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|RepositoryStub
operator|.
name|PROP_STUB_IMPL_CLASS
argument_list|,
name|stubClass
argument_list|)
expr_stmt|;
name|helperPool
operator|.
name|returnHelper
argument_list|(
operator|new
name|RepositoryHelper
argument_list|(
name|props
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|TestCase
name|getTearDown
parameter_list|()
block|{
return|return
operator|new
name|TearDown
argument_list|(
name|helpers
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
class|class
name|TearDown
extends|extends
name|TestCase
block|{
comment|/**          * The repository helpers to restore          */
specifier|private
name|List
argument_list|<
name|RepositoryHelper
argument_list|>
name|helpers
decl_stmt|;
specifier|public
name|TearDown
parameter_list|(
name|List
argument_list|<
name|RepositoryHelper
argument_list|>
name|helpers
parameter_list|)
block|{
name|super
argument_list|(
literal|"testTearDown"
argument_list|)
expr_stmt|;
name|this
operator|.
name|helpers
operator|=
name|helpers
expr_stmt|;
block|}
specifier|public
name|void
name|testTearDown
parameter_list|()
throws|throws
name|Exception
block|{
comment|// restore previous helpers
name|RepositoryHelperPool
name|helperPool
init|=
name|RepositoryHelperPoolImpl
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|helperPool
operator|.
name|borrowHelpers
argument_list|()
expr_stmt|;
for|for
control|(
name|RepositoryHelper
name|helper
range|:
name|helpers
control|)
block|{
name|helperPool
operator|.
name|returnHelper
argument_list|(
name|helper
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

