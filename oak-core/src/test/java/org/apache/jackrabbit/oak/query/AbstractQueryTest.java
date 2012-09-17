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
name|query
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
name|AbstractOakTest
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
name|api
operator|.
name|ContentSession
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
name|CoreValueFactory
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
name|api
operator|.
name|SessionQueryEngine
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
name|core
operator|.
name|ContentRepositoryImpl
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
name|index
operator|.
name|Indexer
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
name|index
operator|.
name|PropertyIndexer
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
name|Before
import|;
end_import

begin_comment
comment|/**  * AbstractQueryTest...  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractQueryTest
extends|extends
name|AbstractOakTest
block|{
specifier|protected
name|CoreValueFactory
name|vf
decl_stmt|;
specifier|protected
name|SessionQueryEngine
name|qe
decl_stmt|;
specifier|protected
name|ContentSession
name|session
decl_stmt|;
specifier|protected
name|Root
name|root
decl_stmt|;
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
return|return
operator|new
name|ContentRepositoryImpl
argument_list|(
name|mk
argument_list|,
name|qip
argument_list|,
name|hook
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|before
argument_list|()
expr_stmt|;
name|session
operator|=
name|createAdminSession
argument_list|()
expr_stmt|;
name|vf
operator|=
name|session
operator|.
name|getCoreValueFactory
argument_list|()
expr_stmt|;
name|qe
operator|=
name|session
operator|.
name|getQueryEngine
argument_list|()
expr_stmt|;
name|root
operator|=
name|session
operator|.
name|getLatestRoot
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

