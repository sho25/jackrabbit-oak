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
name|performance
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
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
name|plugins
operator|.
name|index
operator|.
name|old
operator|.
name|Indexer
import|;
end_import

begin_comment
comment|/**  * A utility class to manage indexes in Oak.  */
end_comment

begin_class
specifier|public
class|class
name|IndexManager
block|{
comment|/**      * The root node of the index definition (configuration) nodes.      */
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_CONFIG_PATH
init|=
name|Indexer
operator|.
name|INDEX_CONFIG_PATH
decl_stmt|;
comment|/**      * Creates a property index for the given property if such an index doesn't      * exist yet, and if the repository supports property indexes. The session      * may not have pending changes.      *       * @param session the session      * @param propertyName the property name      * @return true if the index was created or already existed      */
specifier|public
specifier|static
name|boolean
name|createPropertyIndex
parameter_list|(
name|Session
name|session
parameter_list|,
name|String
name|propertyName
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|createIndex
argument_list|(
name|session
argument_list|,
literal|"property@"
operator|+
name|propertyName
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Node
name|getIndexNode
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Node
name|n
init|=
name|session
operator|.
name|getRootNode
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|e
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|INDEX_CONFIG_PATH
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|n
operator|.
name|hasNode
argument_list|(
name|e
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|n
operator|=
name|n
operator|.
name|getNode
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|n
return|;
block|}
specifier|private
specifier|static
name|boolean
name|createIndex
parameter_list|(
name|Session
name|session
parameter_list|,
name|String
name|indexNodeName
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|session
operator|.
name|hasPendingChanges
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"The session has pending changes"
argument_list|)
throw|;
block|}
name|Node
name|indexes
init|=
name|getIndexNode
argument_list|(
name|session
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexes
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|indexes
operator|.
name|hasNode
argument_list|(
name|indexNodeName
argument_list|)
condition|)
block|{
name|indexes
operator|.
name|addNode
argument_list|(
name|indexNodeName
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

