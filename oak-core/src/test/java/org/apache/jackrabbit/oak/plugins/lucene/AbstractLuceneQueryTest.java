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
name|core
operator|.
name|DefaultConflictHandler
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
name|namepath
operator|.
name|NamePathMapperImpl
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
name|PropertyIndexFactory
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
name|name
operator|.
name|NameValidatorProvider
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
name|name
operator|.
name|NamespaceValidatorProvider
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
name|type
operator|.
name|DefaultTypeEditor
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
name|type
operator|.
name|NodeTypeManagerImpl
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
name|type
operator|.
name|TypeValidatorProvider
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
name|value
operator|.
name|ConflictValidatorProvider
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
name|commit
operator|.
name|CompositeValidatorProvider
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
name|ValidatingHook
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
name|ValidatorProvider
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
name|IndexManager
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
name|IndexManagerImpl
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
name|IndexUtils
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
name|plugins
operator|.
name|lucene
operator|.
name|LuceneIndexUtils
operator|.
name|DEFAULT_INDEX_NAME
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
name|plugins
operator|.
name|lucene
operator|.
name|LuceneIndexUtils
operator|.
name|createIndexNode
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
block|{
specifier|protected
specifier|static
specifier|final
name|String
name|SQL2
init|=
literal|"JCR-SQL2"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_INDEX_NAME
init|=
name|DEFAULT_INDEX_NAME
decl_stmt|;
specifier|protected
name|MicroKernel
name|mk
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
comment|// FIXME workaround to ensure built in node types are registered
operator|new
name|NodeTypeManagerImpl
argument_list|(
name|session
argument_list|,
name|NamePathMapperImpl
operator|.
name|DEFAULT
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|root
operator|=
name|session
operator|.
name|getCurrentRoot
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
name|cleanupIndexNode
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
name|mk
operator|=
operator|new
name|MicroKernelImpl
argument_list|()
expr_stmt|;
return|return
operator|new
name|ContentRepositoryImpl
argument_list|(
name|mk
argument_list|,
operator|new
name|LuceneIndexProvider
argument_list|(
name|DEFAULT_INDEX_HOME
argument_list|)
argument_list|,
name|buildDefaultCommitHook
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|CommitHook
name|buildDefaultCommitHook
parameter_list|()
block|{
name|IndexManager
name|im
init|=
operator|new
name|IndexManagerImpl
argument_list|(
name|IndexUtils
operator|.
name|DEFAULT_INDEX_HOME
argument_list|,
name|mk
argument_list|,
operator|new
name|PropertyIndexFactory
argument_list|()
argument_list|,
operator|new
name|LuceneIndexFactory
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|CommitHook
argument_list|>
name|hooks
init|=
operator|new
name|ArrayList
argument_list|<
name|CommitHook
argument_list|>
argument_list|()
decl_stmt|;
name|hooks
operator|.
name|add
argument_list|(
operator|new
name|DefaultTypeEditor
argument_list|()
argument_list|)
expr_stmt|;
name|hooks
operator|.
name|add
argument_list|(
operator|new
name|ValidatingHook
argument_list|(
name|createDefaultValidatorProvider
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|hooks
operator|.
name|add
argument_list|(
name|im
argument_list|)
expr_stmt|;
return|return
operator|new
name|CompositeHook
argument_list|(
name|hooks
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|ValidatorProvider
name|createDefaultValidatorProvider
parameter_list|()
block|{
name|List
argument_list|<
name|ValidatorProvider
argument_list|>
name|providers
init|=
operator|new
name|ArrayList
argument_list|<
name|ValidatorProvider
argument_list|>
argument_list|()
decl_stmt|;
name|providers
operator|.
name|add
argument_list|(
operator|new
name|NameValidatorProvider
argument_list|()
argument_list|)
expr_stmt|;
name|providers
operator|.
name|add
argument_list|(
operator|new
name|NamespaceValidatorProvider
argument_list|()
argument_list|)
expr_stmt|;
name|providers
operator|.
name|add
argument_list|(
operator|new
name|TypeValidatorProvider
argument_list|()
argument_list|)
expr_stmt|;
name|providers
operator|.
name|add
argument_list|(
operator|new
name|ConflictValidatorProvider
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|CompositeValidatorProvider
argument_list|(
name|providers
argument_list|)
return|;
block|}
comment|/**      * Recreates an empty index node, ready to be used in tests      *       * @throws Exception      */
specifier|private
name|void
name|cleanupIndexNode
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
name|DEFAULT_INDEX_HOME
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|!=
literal|null
condition|)
block|{
name|index
operator|=
name|index
operator|.
name|getChild
argument_list|(
name|TEST_INDEX_NAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|index
operator|!=
literal|null
condition|)
block|{
name|index
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|index
operator|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
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
block|}
name|createIndexNode
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|DEFAULT_INDEX_HOME
argument_list|)
argument_list|,
name|TEST_INDEX_NAME
argument_list|,
name|vf
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|(
name|DefaultConflictHandler
operator|.
name|OURS
argument_list|)
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
literal|null
argument_list|)
return|;
block|}
block|}
end_class

end_unit

