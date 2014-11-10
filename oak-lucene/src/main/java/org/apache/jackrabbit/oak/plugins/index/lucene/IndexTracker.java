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
name|index
operator|.
name|lucene
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkState
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicates
operator|.
name|in
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicates
operator|.
name|not
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicates
operator|.
name|notNull
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|newArrayListWithCapacity
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
operator|.
name|filterKeys
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
operator|.
name|filterValues
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
operator|.
name|newHashMap
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyMap
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
name|index
operator|.
name|lucene
operator|.
name|LuceneIndexConstants
operator|.
name|TYPE_LUCENE
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
name|index
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneIndexHelper
operator|.
name|isLuceneIndexNode
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
name|CompositeEditor
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
name|DefaultEditor
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
name|Editor
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
name|EditorDiff
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
name|SubtreeEditor
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
import|;
end_import

begin_class
class|class
name|IndexTracker
block|{
comment|/** Logger instance. */
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|IndexTracker
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|IndexCopier
name|cloner
decl_stmt|;
specifier|private
name|NodeState
name|root
init|=
name|EMPTY_NODE
decl_stmt|;
specifier|private
specifier|volatile
name|Map
argument_list|<
name|String
argument_list|,
name|IndexNode
argument_list|>
name|indices
init|=
name|emptyMap
argument_list|()
decl_stmt|;
name|IndexTracker
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|IndexTracker
parameter_list|(
name|IndexCopier
name|cloner
parameter_list|)
block|{
name|this
operator|.
name|cloner
operator|=
name|cloner
expr_stmt|;
block|}
specifier|synchronized
name|void
name|close
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|IndexNode
argument_list|>
name|indices
init|=
name|this
operator|.
name|indices
decl_stmt|;
name|this
operator|.
name|indices
operator|=
name|emptyMap
argument_list|()
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|IndexNode
argument_list|>
name|entry
range|:
name|indices
operator|.
name|entrySet
argument_list|()
control|)
block|{
try|try
block|{
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to close the Lucene index at "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|synchronized
name|void
name|update
parameter_list|(
specifier|final
name|NodeState
name|root
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|IndexNode
argument_list|>
name|original
init|=
name|indices
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|IndexNode
argument_list|>
name|updates
init|=
name|newHashMap
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Editor
argument_list|>
name|editors
init|=
name|newArrayListWithCapacity
argument_list|(
name|original
operator|.
name|size
argument_list|()
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
name|IndexNode
argument_list|>
name|entry
range|:
name|original
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|String
name|path
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|editors
operator|.
name|add
argument_list|(
operator|new
name|SubtreeEditor
argument_list|(
operator|new
name|DefaultEditor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|leave
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
try|try
block|{
comment|// TODO: Use DirectoryReader.openIfChanged()
name|IndexNode
name|index
init|=
name|IndexNode
operator|.
name|open
argument_list|(
name|path
argument_list|,
name|root
argument_list|,
name|after
argument_list|,
name|cloner
argument_list|)
decl_stmt|;
name|updates
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|index
argument_list|)
expr_stmt|;
comment|// index can be null
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to open Lucene index at "
operator|+
name|path
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|,
name|Iterables
operator|.
name|toArray
argument_list|(
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|EditorDiff
operator|.
name|process
argument_list|(
name|CompositeEditor
operator|.
name|compose
argument_list|(
name|editors
argument_list|)
argument_list|,
name|this
operator|.
name|root
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
if|if
condition|(
operator|!
name|updates
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|indices
operator|=
name|ImmutableMap
operator|.
expr|<
name|String
operator|,
name|IndexNode
operator|>
name|builder
argument_list|()
operator|.
name|putAll
argument_list|(
name|filterKeys
argument_list|(
name|original
argument_list|,
name|not
argument_list|(
name|in
argument_list|(
name|updates
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|putAll
argument_list|(
name|filterValues
argument_list|(
name|updates
argument_list|,
name|notNull
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
comment|//TODO This might take some time as close need to acquire the
comment|//write lock which might be held by current running searches
for|for
control|(
name|String
name|path
range|:
name|updates
operator|.
name|keySet
argument_list|()
control|)
block|{
name|IndexNode
name|index
init|=
name|original
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
try|try
block|{
name|index
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to close Lucene index at "
operator|+
name|path
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|IndexNode
name|acquireIndexNode
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|IndexNode
name|index
init|=
name|indices
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|!=
literal|null
operator|&&
name|index
operator|.
name|acquire
argument_list|()
condition|)
block|{
return|return
name|index
return|;
block|}
else|else
block|{
return|return
name|findIndexNode
argument_list|(
name|path
argument_list|)
return|;
block|}
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|getIndexNodePaths
parameter_list|()
block|{
return|return
name|indices
operator|.
name|keySet
argument_list|()
return|;
block|}
specifier|private
specifier|synchronized
name|IndexNode
name|findIndexNode
parameter_list|(
name|String
name|path
parameter_list|)
block|{
comment|// Retry the lookup from acquireIndexNode now that we're
comment|// synchronized. The acquire() call is guaranteed to succeed
comment|// since the close() method is also synchronized.
name|IndexNode
name|index
init|=
name|indices
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|!=
literal|null
condition|)
block|{
name|checkState
argument_list|(
name|index
operator|.
name|acquire
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|index
return|;
block|}
name|NodeState
name|node
init|=
name|root
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|node
operator|=
name|node
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|isLuceneIndexNode
argument_list|(
name|node
argument_list|)
condition|)
block|{
name|index
operator|=
name|IndexNode
operator|.
name|open
argument_list|(
name|path
argument_list|,
name|root
argument_list|,
name|node
argument_list|,
name|cloner
argument_list|)
expr_stmt|;
if|if
condition|(
name|index
operator|!=
literal|null
condition|)
block|{
name|checkState
argument_list|(
name|index
operator|.
name|acquire
argument_list|()
argument_list|)
expr_stmt|;
name|indices
operator|=
name|ImmutableMap
operator|.
expr|<
name|String
operator|,
name|IndexNode
operator|>
name|builder
argument_list|()
operator|.
name|putAll
argument_list|(
name|indices
argument_list|)
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|index
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
return|return
name|index
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|node
operator|.
name|exists
argument_list|()
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Cannot open Lucene Index at path {} as the index is not of type {}"
argument_list|,
name|path
argument_list|,
name|TYPE_LUCENE
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Could not access the Lucene index at "
operator|+
name|path
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

