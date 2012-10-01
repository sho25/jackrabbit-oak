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
name|plugins
operator|.
name|lucene
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
name|CommitFailedException
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
name|IndexDefinition
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|state
operator|.
name|NodeState
import|;
end_import

begin_class
specifier|public
class|class
name|LuceneHook
implements|implements
name|CommitHook
implements|,
name|LuceneIndexConstants
block|{
specifier|private
specifier|final
name|String
name|indexConfigPath
decl_stmt|;
specifier|public
name|LuceneHook
parameter_list|(
name|String
name|indexConfigPath
parameter_list|)
block|{
name|this
operator|.
name|indexConfigPath
operator|=
name|indexConfigPath
expr_stmt|;
block|}
comment|/**      * TODO test only      */
specifier|public
name|LuceneHook
parameter_list|()
block|{
name|this
argument_list|(
name|IndexUtils
operator|.
name|DEFAULT_INDEX_HOME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|processCommit
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
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
name|List
argument_list|<
name|IndexDefinition
argument_list|>
name|indexDefinitions
init|=
name|IndexUtils
operator|.
name|buildIndexDefinitions
argument_list|(
name|after
argument_list|,
name|indexConfigPath
argument_list|,
name|TYPE
argument_list|)
decl_stmt|;
for|for
control|(
name|IndexDefinition
name|def
range|:
name|indexDefinitions
control|)
block|{
name|hooks
operator|.
name|add
argument_list|(
operator|new
name|LuceneEditor
argument_list|(
name|def
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|CompositeHook
operator|.
name|compose
argument_list|(
name|hooks
argument_list|)
operator|.
name|processCommit
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
return|;
block|}
block|}
end_class

end_unit

