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
name|INDEX_DATA_CHILD_NAME
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
name|PERSISTENCE_FILE
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
name|PERSISTENCE_NAME
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
name|PERSISTENCE_PATH
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|concurrent
operator|.
name|locks
operator|.
name|ReadWriteLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
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
name|ReadOnlyBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|DirectoryReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|IndexSearcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|FSDirectory
import|;
end_import

begin_class
class|class
name|IndexNode
block|{
specifier|static
name|IndexNode
name|open
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|definition
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|directory
init|=
literal|null
decl_stmt|;
name|NodeState
name|data
init|=
name|definition
operator|.
name|getChildNode
argument_list|(
name|INDEX_DATA_CHILD_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|.
name|exists
argument_list|()
condition|)
block|{
name|directory
operator|=
operator|new
name|OakDirectory
argument_list|(
operator|new
name|ReadOnlyBuilder
argument_list|(
name|data
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|PERSISTENCE_FILE
operator|.
name|equalsIgnoreCase
argument_list|(
name|definition
operator|.
name|getString
argument_list|(
name|PERSISTENCE_NAME
argument_list|)
argument_list|)
condition|)
block|{
name|String
name|path
init|=
name|definition
operator|.
name|getString
argument_list|(
name|PERSISTENCE_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
operator|&&
operator|new
name|File
argument_list|(
name|path
argument_list|)
operator|.
name|exists
argument_list|()
condition|)
block|{
name|directory
operator|=
name|FSDirectory
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|directory
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|IndexNode
name|index
init|=
operator|new
name|IndexNode
argument_list|(
name|name
argument_list|,
name|definition
argument_list|,
name|directory
argument_list|)
decl_stmt|;
name|directory
operator|=
literal|null
expr_stmt|;
comment|// closed in Index.close()
return|return
name|index
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|directory
operator|!=
literal|null
condition|)
block|{
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|definition
decl_stmt|;
specifier|private
specifier|final
name|Directory
name|directory
decl_stmt|;
specifier|private
specifier|final
name|IndexReader
name|reader
decl_stmt|;
specifier|private
specifier|final
name|IndexSearcher
name|searcher
decl_stmt|;
specifier|private
specifier|final
name|ReadWriteLock
name|lock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
name|IndexNode
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|definition
parameter_list|,
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|definition
operator|=
name|definition
expr_stmt|;
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|this
operator|.
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
name|NodeState
name|getDefinition
parameter_list|()
block|{
return|return
name|definition
return|;
block|}
name|IndexSearcher
name|getSearcher
parameter_list|()
block|{
return|return
name|searcher
return|;
block|}
name|boolean
name|acquire
parameter_list|()
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
if|if
condition|(
name|closed
condition|)
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
name|void
name|release
parameter_list|()
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|checkState
argument_list|(
operator|!
name|closed
argument_list|)
expr_stmt|;
name|closed
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

