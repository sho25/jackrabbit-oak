begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|indexversion
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
name|api
operator|.
name|Type
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
name|IndexPathService
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
name|IndexPathServiceImpl
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
name|IndexUpdateProvider
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
name|property
operator|.
name|PropertyIndexEditorProvider
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
name|search
operator|.
name|spi
operator|.
name|query
operator|.
name|IndexName
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
name|run
operator|.
name|cli
operator|.
name|NodeStoreFixture
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
name|run
operator|.
name|cli
operator|.
name|NodeStoreFixtureProvider
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
name|run
operator|.
name|cli
operator|.
name|Options
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
name|CommitInfo
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
name|EditorHook
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
name|NodeBuilder
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
name|NodeStateUtils
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
name|NodeStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_class
specifier|public
class|class
name|PurgeOldIndexVersion
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PurgeOldIndexVersion
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|void
name|execute
parameter_list|(
name|Options
name|opts
parameter_list|,
name|long
name|purgeThresholdMillis
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|indexPaths
parameter_list|)
throws|throws
name|Exception
block|{
name|boolean
name|isReadWriteRepository
init|=
name|opts
operator|.
name|getCommonOpts
argument_list|()
operator|.
name|isReadWrite
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|isReadWriteRepository
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Repository connected in read-only mode. Use '--read-write' for write operations"
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|NodeStoreFixture
name|fixture
init|=
name|NodeStoreFixtureProvider
operator|.
name|create
argument_list|(
name|opts
argument_list|)
init|)
block|{
name|NodeStore
name|nodeStore
init|=
name|fixture
operator|.
name|getStore
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|sanitisedIndexPaths
init|=
name|sanitiseUserIndexPaths
argument_list|(
name|indexPaths
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|indexPathSet
init|=
name|filterIndexPaths
argument_list|(
name|getRepositoryIndexPaths
argument_list|(
name|nodeStore
argument_list|)
argument_list|,
name|sanitisedIndexPaths
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|segregateIndexes
init|=
name|segregateIndexes
argument_list|(
name|indexPathSet
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|entry
range|:
name|segregateIndexes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|baseIndexPath
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|parentPath
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|IndexName
argument_list|>
name|indexNameObjectList
init|=
name|getIndexNameObjectList
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|NodeState
name|indexDefParentNode
init|=
name|NodeStateUtils
operator|.
name|getNode
argument_list|(
name|nodeStore
operator|.
name|getRoot
argument_list|()
argument_list|,
name|parentPath
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|IndexVersionOperation
argument_list|>
name|toDeleteIndexNameObjectList
init|=
name|IndexVersionOperation
operator|.
name|generateIndexVersionOperationList
argument_list|(
name|indexDefParentNode
argument_list|,
name|indexNameObjectList
argument_list|,
name|purgeThresholdMillis
argument_list|)
decl_stmt|;
if|if
condition|(
name|isReadWriteRepository
operator|&&
operator|!
name|toDeleteIndexNameObjectList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|purgeOldIndexVersion
argument_list|(
name|nodeStore
argument_list|,
name|toDeleteIndexNameObjectList
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Repository is opened in read-only mode: IndexOperations"
operator|+
literal|" for index at path {} are : {}"
argument_list|,
name|baseIndexPath
argument_list|,
name|toDeleteIndexNameObjectList
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**      * @param userIndexPaths indexpaths provided by user      * @return a list of Indexpaths having baseIndexpaths or path till oak:index      * @throws IllegalArgumentException if the paths provided are not till oak:index or till index      */
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|sanitiseUserIndexPaths
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|userIndexPaths
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|sanitisedUserIndexPaths
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|userIndexPath
range|:
name|userIndexPaths
control|)
block|{
if|if
condition|(
name|PathUtils
operator|.
name|getName
argument_list|(
name|userIndexPath
argument_list|)
operator|.
name|equals
argument_list|(
name|PurgeOldVersionUtils
operator|.
name|OAK_INDEX
argument_list|)
condition|)
block|{
name|sanitisedUserIndexPaths
operator|.
name|add
argument_list|(
name|userIndexPath
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|PathUtils
operator|.
name|getName
argument_list|(
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|userIndexPath
argument_list|)
argument_list|)
operator|.
name|equals
argument_list|(
name|PurgeOldVersionUtils
operator|.
name|OAK_INDEX
argument_list|)
condition|)
block|{
name|sanitisedUserIndexPaths
operator|.
name|add
argument_list|(
name|IndexName
operator|.
name|parse
argument_list|(
name|userIndexPath
argument_list|)
operator|.
name|getBaseName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|userIndexPath
operator|+
literal|" indexpath is not valid"
argument_list|)
throw|;
block|}
block|}
return|return
name|sanitisedUserIndexPaths
return|;
block|}
comment|/**      * @param indexPathSet      * @return a map with baseIndexName as key and a set of indexpaths having same baseIndexName      */
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|segregateIndexes
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|indexPathSet
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|segregatedIndexes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|indexPathSet
control|)
block|{
name|String
name|baseIndexPath
init|=
name|IndexName
operator|.
name|parse
argument_list|(
name|path
argument_list|)
operator|.
name|getBaseName
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|indexPaths
init|=
name|segregatedIndexes
operator|.
name|get
argument_list|(
name|baseIndexPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexPaths
operator|==
literal|null
condition|)
block|{
name|indexPaths
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|indexPaths
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|segregatedIndexes
operator|.
name|put
argument_list|(
name|baseIndexPath
argument_list|,
name|indexPaths
argument_list|)
expr_stmt|;
block|}
return|return
name|segregatedIndexes
return|;
block|}
specifier|private
name|Iterable
argument_list|<
name|String
argument_list|>
name|getRepositoryIndexPaths
parameter_list|(
name|NodeStore
name|store
parameter_list|)
throws|throws
name|CommitFailedException
throws|,
name|IOException
block|{
name|IndexPathService
name|indexPathService
init|=
operator|new
name|IndexPathServiceImpl
argument_list|(
name|store
argument_list|)
decl_stmt|;
return|return
name|indexPathService
operator|.
name|getIndexPaths
argument_list|()
return|;
block|}
comment|/**      * @param repositoryIndexPaths: list of indexpaths retrieved from  index service      * @param commandlineIndexPaths indexpaths provided by user      * @return returns set of indexpaths which are to be considered for purging      */
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|filterIndexPaths
parameter_list|(
name|Iterable
argument_list|<
name|String
argument_list|>
name|repositoryIndexPaths
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|commandlineIndexPaths
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|filteredIndexPaths
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|commandlineIndexPath
range|:
name|commandlineIndexPaths
control|)
block|{
for|for
control|(
name|String
name|repositoryIndexPath
range|:
name|repositoryIndexPaths
control|)
block|{
if|if
condition|(
name|PurgeOldVersionUtils
operator|.
name|isIndexChildNode
argument_list|(
name|commandlineIndexPath
argument_list|,
name|repositoryIndexPath
argument_list|)
operator|||
name|PurgeOldVersionUtils
operator|.
name|isBaseIndexEqual
argument_list|(
name|commandlineIndexPath
argument_list|,
name|repositoryIndexPath
argument_list|)
condition|)
block|{
name|filteredIndexPaths
operator|.
name|add
argument_list|(
name|repositoryIndexPath
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|filteredIndexPaths
return|;
block|}
specifier|private
name|List
argument_list|<
name|IndexName
argument_list|>
name|getIndexNameObjectList
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|versionedIndexPaths
parameter_list|)
block|{
name|List
argument_list|<
name|IndexName
argument_list|>
name|indexNameObjectList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|indexNameString
range|:
name|versionedIndexPaths
control|)
block|{
name|indexNameObjectList
operator|.
name|add
argument_list|(
name|IndexName
operator|.
name|parse
argument_list|(
name|indexNameString
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|indexNameObjectList
return|;
block|}
specifier|private
name|void
name|purgeOldIndexVersion
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|List
argument_list|<
name|IndexVersionOperation
argument_list|>
name|toDeleteIndexNameObjectList
parameter_list|)
throws|throws
name|CommitFailedException
block|{
for|for
control|(
name|IndexVersionOperation
name|toDeleteIndexNameObject
range|:
name|toDeleteIndexNameObjectList
control|)
block|{
name|NodeState
name|root
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|NodeBuilder
name|rootBuilder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|nodeBuilder
init|=
name|PurgeOldVersionUtils
operator|.
name|getNode
argument_list|(
name|rootBuilder
argument_list|,
name|toDeleteIndexNameObject
operator|.
name|getIndexName
argument_list|()
operator|.
name|getNodeName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeBuilder
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
name|toDeleteIndexNameObject
operator|.
name|getOperation
argument_list|()
operator|==
name|IndexVersionOperation
operator|.
name|Operation
operator|.
name|DELETE_HIDDEN_AND_DISABLE
condition|)
block|{
name|nodeBuilder
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"disabled"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|PurgeOldVersionUtils
operator|.
name|recursiveDeleteHiddenChildNodes
argument_list|(
name|store
argument_list|,
name|toDeleteIndexNameObject
operator|.
name|getIndexName
argument_list|()
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|toDeleteIndexNameObject
operator|.
name|getOperation
argument_list|()
operator|==
name|IndexVersionOperation
operator|.
name|Operation
operator|.
name|DELETE
condition|)
block|{
name|nodeBuilder
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|EditorHook
name|hook
init|=
operator|new
name|EditorHook
argument_list|(
operator|new
name|IndexUpdateProvider
argument_list|(
operator|new
name|PropertyIndexEditorProvider
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|rootBuilder
argument_list|,
name|hook
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"nodebuilder null for path "
operator|+
name|toDeleteIndexNameObject
operator|.
name|getIndexName
argument_list|()
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

