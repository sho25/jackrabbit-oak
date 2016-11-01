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
name|plugins
operator|.
name|segment
operator|.
name|file
operator|.
name|tooling
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
name|collect
operator|.
name|Sets
operator|.
name|newHashSet
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Math
operator|.
name|min
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
name|api
operator|.
name|Type
operator|.
name|BINARIES
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
name|api
operator|.
name|Type
operator|.
name|BINARY
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
name|commons
operator|.
name|PathUtils
operator|.
name|concat
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
name|commons
operator|.
name|PathUtils
operator|.
name|denotesRoot
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
name|commons
operator|.
name|PathUtils
operator|.
name|getName
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
name|commons
operator|.
name|PathUtils
operator|.
name|getParentPath
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
name|state
operator|.
name|NodeStateUtils
operator|.
name|getNode
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
name|io
operator|.
name|InputStream
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
name|api
operator|.
name|Blob
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
name|PropertyState
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
name|plugins
operator|.
name|segment
operator|.
name|SegmentBlob
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
name|segment
operator|.
name|SegmentNodeStore
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
name|segment
operator|.
name|file
operator|.
name|FileStore
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
name|segment
operator|.
name|file
operator|.
name|FileStore
operator|.
name|ReadOnlyStore
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
name|segment
operator|.
name|file
operator|.
name|InvalidFileStoreVersionException
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
name|segment
operator|.
name|file
operator|.
name|JournalReader
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
name|ChildNodeEntry
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

begin_comment
comment|/**  * Utility for checking the files of a  * {@link org.apache.jackrabbit.oak.plugins.segment.file.FileStore} for inconsistency and  * reporting that latest consistent revision.  */
end_comment

begin_class
annotation|@
name|Deprecated
specifier|public
class|class
name|ConsistencyChecker
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
name|ConsistencyChecker
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ReadOnlyStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|long
name|debugInterval
decl_stmt|;
comment|/**      * Run a consistency check.      *      * @param directory  directory containing the tar files      * @param journalFileName  name of the journal file containing the revision history      * @param fullTraversal    full traversal consistency check if {@code true}. Only try      *                         to access the root node otherwise.      * @param debugInterval    number of seconds between printing progress information to      *                         the console during the full traversal phase.      * @param binLen           number of bytes to read from binary properties. -1 for all.      * @return  the latest consistent revision out of the revisions listed in the journal.      * @throws IOException      */
annotation|@
name|Deprecated
specifier|public
specifier|static
name|String
name|checkConsistency
parameter_list|(
name|File
name|directory
parameter_list|,
name|String
name|journalFileName
parameter_list|,
name|boolean
name|fullTraversal
parameter_list|,
name|long
name|debugInterval
parameter_list|,
name|long
name|binLen
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
block|{
name|print
argument_list|(
literal|"Searching for last good revision in {}"
argument_list|,
name|journalFileName
argument_list|)
expr_stmt|;
name|JournalReader
name|journal
init|=
operator|new
name|JournalReader
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|journalFileName
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|badPaths
init|=
name|newHashSet
argument_list|()
decl_stmt|;
name|ConsistencyChecker
name|checker
init|=
operator|new
name|ConsistencyChecker
argument_list|(
name|directory
argument_list|,
name|debugInterval
argument_list|)
decl_stmt|;
try|try
block|{
name|int
name|revisionCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|revision
range|:
name|journal
control|)
block|{
try|try
block|{
name|print
argument_list|(
literal|"Checking revision {}"
argument_list|,
name|revision
argument_list|)
expr_stmt|;
name|revisionCount
operator|++
expr_stmt|;
name|String
name|badPath
init|=
name|checker
operator|.
name|check
argument_list|(
name|revision
argument_list|,
name|badPaths
argument_list|,
name|binLen
argument_list|)
decl_stmt|;
if|if
condition|(
name|badPath
operator|==
literal|null
operator|&&
name|fullTraversal
condition|)
block|{
name|badPath
operator|=
name|checker
operator|.
name|traverse
argument_list|(
name|revision
argument_list|,
name|binLen
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|badPath
operator|==
literal|null
condition|)
block|{
name|print
argument_list|(
literal|"Found latest good revision {}"
argument_list|,
name|revision
argument_list|)
expr_stmt|;
name|print
argument_list|(
literal|"Searched through {} revisions"
argument_list|,
name|revisionCount
argument_list|)
expr_stmt|;
return|return
name|revision
return|;
block|}
else|else
block|{
name|badPaths
operator|.
name|add
argument_list|(
name|badPath
argument_list|)
expr_stmt|;
name|print
argument_list|(
literal|"Broken revision {}"
argument_list|,
name|revision
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|print
argument_list|(
literal|"Skipping invalid record id {}"
argument_list|,
name|revision
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|checker
operator|.
name|close
argument_list|()
expr_stmt|;
name|journal
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|print
argument_list|(
literal|"No good revision found"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|/**      * Create a new consistency checker instance      *      * @param directory  directory containing the tar files      * @param debugInterval    number of seconds between printing progress information to      *                         the console during the full traversal phase.      * @throws IOException      */
annotation|@
name|Deprecated
specifier|public
name|ConsistencyChecker
parameter_list|(
name|File
name|directory
parameter_list|,
name|long
name|debugInterval
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
block|{
name|store
operator|=
name|FileStore
operator|.
name|builder
argument_list|(
name|directory
argument_list|)
operator|.
name|buildReadOnly
argument_list|()
expr_stmt|;
name|this
operator|.
name|debugInterval
operator|=
name|debugInterval
expr_stmt|;
block|}
comment|/**      * Check whether the nodes and all its properties of all given      * {@code paths} are consistent at the given {@code revision}.      *      * @param revision  revision to check      * @param paths     paths to check      * @param binLen    number of bytes to read from binary properties. -1 for all.      * @return  Path of the first inconsistency detected or {@code null} if none.      */
annotation|@
name|Deprecated
specifier|public
name|String
name|check
parameter_list|(
name|String
name|revision
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|,
name|long
name|binLen
parameter_list|)
block|{
name|store
operator|.
name|setRevision
argument_list|(
name|revision
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
name|String
name|err
init|=
name|checkPath
argument_list|(
name|path
argument_list|,
name|binLen
argument_list|)
decl_stmt|;
if|if
condition|(
name|err
operator|!=
literal|null
condition|)
block|{
return|return
name|err
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|String
name|checkPath
parameter_list|(
name|String
name|path
parameter_list|,
name|long
name|binLen
parameter_list|)
block|{
try|try
block|{
name|print
argument_list|(
literal|"Checking {}"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|NodeState
name|root
init|=
name|SegmentNodeStore
operator|.
name|builder
argument_list|(
name|store
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|String
name|parentPath
init|=
name|getParentPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|getName
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|NodeState
name|parent
init|=
name|getNode
argument_list|(
name|root
argument_list|,
name|parentPath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|denotesRoot
argument_list|(
name|path
argument_list|)
operator|&&
name|parent
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|traverse
argument_list|(
name|parent
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|,
name|path
argument_list|,
literal|false
argument_list|,
name|binLen
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|traverse
argument_list|(
name|parent
argument_list|,
name|parentPath
argument_list|,
literal|false
argument_list|,
name|binLen
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|print
argument_list|(
literal|"Error while checking {}: {}"
argument_list|,
name|path
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|path
return|;
block|}
block|}
specifier|private
name|int
name|nodeCount
decl_stmt|;
specifier|private
name|int
name|propertyCount
decl_stmt|;
comment|/**      * Travers the given {@code revision}      * @param revision  revision to travers      * @param binLen    number of bytes to read from binary properties. -1 for all.      */
annotation|@
name|Deprecated
specifier|public
name|String
name|traverse
parameter_list|(
name|String
name|revision
parameter_list|,
name|long
name|binLen
parameter_list|)
block|{
try|try
block|{
name|store
operator|.
name|setRevision
argument_list|(
name|revision
argument_list|)
expr_stmt|;
name|nodeCount
operator|=
literal|0
expr_stmt|;
name|propertyCount
operator|=
literal|0
expr_stmt|;
name|String
name|result
init|=
name|traverse
argument_list|(
name|SegmentNodeStore
operator|.
name|builder
argument_list|(
name|store
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|getRoot
argument_list|()
argument_list|,
literal|"/"
argument_list|,
literal|true
argument_list|,
name|binLen
argument_list|)
decl_stmt|;
name|print
argument_list|(
literal|"Traversed {} nodes and {} properties"
argument_list|,
name|nodeCount
argument_list|,
name|propertyCount
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|print
argument_list|(
literal|"Error while traversing {}"
argument_list|,
name|revision
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|"/"
return|;
block|}
block|}
specifier|private
name|String
name|traverse
parameter_list|(
name|NodeState
name|node
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|deep
parameter_list|,
name|long
name|binLen
parameter_list|)
block|{
try|try
block|{
name|debug
argument_list|(
literal|"Traversing {}"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|nodeCount
operator|++
expr_stmt|;
for|for
control|(
name|PropertyState
name|propertyState
range|:
name|node
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|debug
argument_list|(
literal|"Checking {}/{}"
argument_list|,
name|path
argument_list|,
name|propertyState
argument_list|)
expr_stmt|;
name|Type
argument_list|<
name|?
argument_list|>
name|type
init|=
name|propertyState
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|BINARY
condition|)
block|{
name|traverse
argument_list|(
name|propertyState
operator|.
name|getValue
argument_list|(
name|BINARY
argument_list|)
argument_list|,
name|binLen
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|BINARIES
condition|)
block|{
for|for
control|(
name|Blob
name|blob
range|:
name|propertyState
operator|.
name|getValue
argument_list|(
name|BINARIES
argument_list|)
control|)
block|{
name|traverse
argument_list|(
name|blob
argument_list|,
name|binLen
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|propertyState
operator|.
name|getValue
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
name|propertyCount
operator|++
expr_stmt|;
block|}
for|for
control|(
name|ChildNodeEntry
name|cne
range|:
name|node
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|String
name|childName
init|=
name|cne
operator|.
name|getName
argument_list|()
decl_stmt|;
name|NodeState
name|child
init|=
name|cne
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
if|if
condition|(
name|deep
condition|)
block|{
name|String
name|result
init|=
name|traverse
argument_list|(
name|child
argument_list|,
name|concat
argument_list|(
name|path
argument_list|,
name|childName
argument_list|)
argument_list|,
literal|true
argument_list|,
name|binLen
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|print
argument_list|(
literal|"Error while traversing {}: {}"
argument_list|,
name|path
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|path
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|print
argument_list|(
literal|"Error while traversing {}: {}"
argument_list|,
name|path
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|path
return|;
block|}
block|}
specifier|private
specifier|static
name|void
name|traverse
parameter_list|(
name|Blob
name|blob
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|length
operator|<
literal|0
condition|)
block|{
name|length
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
if|if
condition|(
name|length
operator|>
literal|0
operator|&&
operator|!
name|isExternal
argument_list|(
name|blob
argument_list|)
condition|)
block|{
name|InputStream
name|s
init|=
name|blob
operator|.
name|getNewStream
argument_list|()
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|8192
index|]
decl_stmt|;
name|int
name|l
init|=
name|s
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
operator|(
name|int
operator|)
name|min
argument_list|(
name|buffer
operator|.
name|length
argument_list|,
name|length
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
name|l
operator|>=
literal|0
operator|&&
operator|(
name|length
operator|-=
name|l
operator|)
operator|>
literal|0
condition|)
block|{
name|l
operator|=
name|s
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
operator|(
name|int
operator|)
name|min
argument_list|(
name|buffer
operator|.
name|length
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|s
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|boolean
name|isExternal
parameter_list|(
name|Blob
name|b
parameter_list|)
block|{
if|if
condition|(
name|b
operator|instanceof
name|SegmentBlob
condition|)
block|{
return|return
operator|(
operator|(
name|SegmentBlob
operator|)
name|b
operator|)
operator|.
name|isExternal
argument_list|()
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Deprecated
specifier|public
name|void
name|close
parameter_list|()
block|{
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|print
parameter_list|(
name|String
name|format
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|format
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|print
parameter_list|(
name|String
name|format
parameter_list|,
name|Object
name|arg
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|format
argument_list|,
name|arg
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|print
parameter_list|(
name|String
name|format
parameter_list|,
name|Object
name|arg1
parameter_list|,
name|Object
name|arg2
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|format
argument_list|,
name|arg1
argument_list|,
name|arg2
argument_list|)
expr_stmt|;
block|}
specifier|private
name|long
name|ts
decl_stmt|;
specifier|private
name|void
name|debug
parameter_list|(
name|String
name|format
parameter_list|,
name|Object
name|arg
parameter_list|)
block|{
if|if
condition|(
name|debug
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|format
argument_list|,
name|arg
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|debug
parameter_list|(
name|String
name|format
parameter_list|,
name|Object
name|arg1
parameter_list|,
name|Object
name|arg2
parameter_list|)
block|{
if|if
condition|(
name|debug
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|format
argument_list|,
name|arg1
argument_list|,
name|arg2
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|debug
parameter_list|()
block|{
comment|// Avoid calling System.currentTimeMillis(), which is slow on some systems.
if|if
condition|(
name|debugInterval
operator|==
name|Long
operator|.
name|MAX_VALUE
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|debugInterval
operator|==
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
name|long
name|ts
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|ts
operator|-
name|this
operator|.
name|ts
operator|)
operator|/
literal|1000
operator|>
name|debugInterval
condition|)
block|{
name|this
operator|.
name|ts
operator|=
name|ts
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

