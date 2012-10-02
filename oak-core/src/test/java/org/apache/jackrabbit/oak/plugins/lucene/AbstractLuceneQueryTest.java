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
name|plugins
operator|.
name|lucene
package|;
end_package

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
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
name|api
operator|.
name|MicroKernel
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
name|Result
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
name|commons
operator|.
name|PathUtils
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
name|CommitHook
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

begin_import
import|import static
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
name|IndexUtils
operator|.
name|DEFAULT_INDEX_HOME
import|;
end_import

begin_comment
comment|/**  * base class for lucene search tests  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractLuceneQueryTest
extends|extends
name|AbstractOakTest
implements|implements
name|LuceneIndexConstants
block|{
specifier|protected
specifier|static
specifier|final
name|String
name|SQL2
init|=
literal|"JCR-SQL2"
decl_stmt|;
specifier|protected
name|ContentSession
name|session
decl_stmt|;
specifier|protected
name|CoreValueFactory
name|vf
decl_stmt|;
specifier|protected
name|SessionQueryEngine
name|qe
decl_stmt|;
specifier|protected
name|Root
name|root
decl_stmt|;
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
name|root
operator|=
name|session
operator|.
name|getLatestRoot
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
name|root
operator|.
name|getQueryEngine
argument_list|()
expr_stmt|;
name|createIndexNode
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|ContentRepository
name|createRepository
parameter_list|()
block|{
name|QueryIndexProvider
name|qip
init|=
operator|new
name|CompositeQueryIndexProvider
argument_list|(
operator|new
name|LuceneIndexProvider
argument_list|(
name|DEFAULT_INDEX_HOME
argument_list|)
argument_list|)
decl_stmt|;
name|CommitHook
name|ch
init|=
operator|new
name|CompositeHook
argument_list|(
operator|new
name|LuceneReindexHook
argument_list|(
name|DEFAULT_INDEX_HOME
argument_list|)
argument_list|,
operator|new
name|LuceneHook
argument_list|(
name|DEFAULT_INDEX_HOME
argument_list|)
argument_list|)
decl_stmt|;
name|MicroKernel
name|mk
init|=
operator|new
name|MicroKernelImpl
argument_list|()
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
name|ch
argument_list|)
operator|.
name|createContentRepository
argument_list|()
return|;
block|}
specifier|protected
name|void
name|createIndexNode
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|index
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|DEFAULT_INDEX_HOME
argument_list|)
control|)
block|{
if|if
condition|(
name|index
operator|.
name|hasChild
argument_list|(
name|p
argument_list|)
condition|)
block|{
name|index
operator|=
name|index
operator|.
name|getChild
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|index
operator|=
name|index
operator|.
name|addChild
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
name|index
operator|.
name|addChild
argument_list|(
literal|"test-lucene"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"lucene"
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|Result
name|executeQuery
parameter_list|(
name|String
name|statement
parameter_list|)
throws|throws
name|ParseException
block|{
return|return
name|qe
operator|.
name|executeQuery
argument_list|(
name|statement
argument_list|,
name|SQL2
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
name|session
operator|.
name|getLatestRoot
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class

end_unit

