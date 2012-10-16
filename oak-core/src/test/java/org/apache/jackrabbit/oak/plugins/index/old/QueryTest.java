begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law  * or agreed to in writing, software distributed under the License is  * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied. See the License for the specific language  * governing permissions and limitations under the License.  */
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
name|plugins
operator|.
name|index
operator|.
name|old
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
name|mk
operator|.
name|core
operator|.
name|MicroKernelImpl
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
name|mk
operator|.
name|index
operator|.
name|IndexWrapper
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
name|Oak
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
name|query
operator|.
name|AbstractQueryTest
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
name|commit
operator|.
name|CompositeHook
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
name|query
operator|.
name|CompositeQueryIndexProvider
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
name|query
operator|.
name|QueryIndexProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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

begin_comment
comment|/**  * Test the query feature.  */
end_comment

begin_class
specifier|public
class|class
name|QueryTest
extends|extends
name|AbstractQueryTest
block|{
annotation|@
name|Override
specifier|protected
name|ContentRepository
name|createRepository
parameter_list|()
block|{
comment|// the property and prefix index currently require the index wrapper
name|IndexWrapper
name|mk
init|=
operator|new
name|IndexWrapper
argument_list|(
operator|new
name|MicroKernelImpl
argument_list|()
argument_list|,
name|TEST_INDEX_HOME
operator|+
name|INDEX_DEFINITIONS_NAME
operator|+
literal|"/indexes"
argument_list|)
decl_stmt|;
name|Indexer
name|indexer
init|=
name|mk
operator|.
name|getIndexer
argument_list|()
decl_stmt|;
comment|// MicroKernel mk = new MicroKernelImpl();
comment|// Indexer indexer = new Indexer(mk);
name|PropertyIndexer
name|pi
init|=
operator|new
name|PropertyIndexer
argument_list|(
name|indexer
argument_list|)
decl_stmt|;
name|QueryIndexProvider
name|qip
init|=
operator|new
name|CompositeQueryIndexProvider
argument_list|(
name|pi
argument_list|)
decl_stmt|;
name|CompositeHook
name|hook
init|=
operator|new
name|CompositeHook
argument_list|(
name|pi
argument_list|)
decl_stmt|;
name|createDefaultKernelTracker
argument_list|()
operator|.
name|available
argument_list|(
name|mk
argument_list|)
expr_stmt|;
return|return
operator|new
name|Oak
argument_list|(
name|mk
argument_list|)
operator|.
name|with
argument_list|(
name|qip
argument_list|)
operator|.
name|with
argument_list|(
name|hook
argument_list|)
operator|.
name|with
argument_list|(
name|getSecurityProvider
argument_list|()
argument_list|)
operator|.
name|createContentRepository
argument_list|()
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|sql2Explain
parameter_list|()
throws|throws
name|Exception
block|{
name|test
argument_list|(
literal|"sql2_explain.txt"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"OAK-288 prevents the index from seeing updates that happened directly on the mk"
argument_list|)
specifier|public
name|void
name|sql2
parameter_list|()
throws|throws
name|Exception
block|{
name|test
argument_list|(
literal|"sql2.txt"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

