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
name|Maps
operator|.
name|newHashMap
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
name|IOUtils
operator|.
name|humanReadableByteCount
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
name|segment
operator|.
name|file
operator|.
name|FileStoreBuilder
operator|.
name|fileStoreBuilder
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
name|Closeable
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
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|MessageFormat
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|segment
operator|.
name|SegmentNodeStoreBuilders
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
name|segment
operator|.
name|file
operator|.
name|FileStoreBuilder
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
name|segment
operator|.
name|file
operator|.
name|IOMonitorAdapter
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
name|segment
operator|.
name|file
operator|.
name|ReadOnlyFileStore
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

begin_comment
comment|/**  * Utility for checking the files of a  * {@link FileStore} for inconsistency and  * reporting that latest consistent revision.  */
end_comment

begin_class
specifier|public
class|class
name|ConsistencyChecker
implements|implements
name|Closeable
block|{
specifier|private
specifier|static
class|class
name|StatisticsIOMonitor
extends|extends
name|IOMonitorAdapter
block|{
specifier|private
specifier|final
name|AtomicLong
name|ioOperations
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|AtomicLong
name|readBytes
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|AtomicLong
name|readTime
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|afterSegmentRead
parameter_list|(
name|File
name|file
parameter_list|,
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|,
name|int
name|length
parameter_list|,
name|long
name|elapsed
parameter_list|)
block|{
name|ioOperations
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|readBytes
operator|.
name|addAndGet
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|readTime
operator|.
name|addAndGet
argument_list|(
name|elapsed
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|final
name|StatisticsIOMonitor
name|statisticsIOMonitor
init|=
operator|new
name|StatisticsIOMonitor
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ReadOnlyFileStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|long
name|debugInterval
decl_stmt|;
specifier|private
specifier|final
name|PrintWriter
name|outWriter
decl_stmt|;
specifier|private
specifier|final
name|PrintWriter
name|errWriter
decl_stmt|;
specifier|private
name|int
name|nodeCount
decl_stmt|;
specifier|private
name|int
name|propertyCount
decl_stmt|;
comment|/**      * Run a full traversal consistency check.      *      * @param directory  directory containing the tar files      * @param journalFileName  name of the journal file containing the revision history      * @param debugInterval    number of seconds between printing progress information to      *                         the console during the full traversal phase.      * @param checkBinaries    if {@code true} full content of binary properties will be scanned      * @param filterPaths      collection of repository paths to be checked                               * @param ioStatistics     if {@code true} prints I/O statistics gathered while consistency       *                         check was performed      * @param outWriter        text output stream writer      * @param errWriter        text error stream writer                              * @throws IOException      */
specifier|public
specifier|static
name|void
name|checkConsistency
parameter_list|(
name|File
name|directory
parameter_list|,
name|String
name|journalFileName
parameter_list|,
name|long
name|debugInterval
parameter_list|,
name|boolean
name|checkBinaries
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|filterPaths
parameter_list|,
name|boolean
name|ioStatistics
parameter_list|,
name|PrintWriter
name|outWriter
parameter_list|,
name|PrintWriter
name|errWriter
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
block|{
try|try
init|(
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
init|;
name|ConsistencyChecker
name|checker
operator|=
operator|new
name|ConsistencyChecker
argument_list|(
name|directory
argument_list|,
name|debugInterval
argument_list|,
name|ioStatistics
argument_list|,
name|outWriter
argument_list|,
name|errWriter
argument_list|)
init|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|pathToValidRevision
init|=
name|newHashMap
argument_list|()
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
name|pathToCorruptPaths
init|=
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|filterPaths
control|)
block|{
name|pathToCorruptPaths
operator|.
name|put
argument_list|(
name|path
argument_list|,
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|int
name|count
init|=
literal|0
decl_stmt|;
name|int
name|revisionCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|journal
operator|.
name|hasNext
argument_list|()
operator|&&
name|count
operator|<
name|filterPaths
operator|.
name|size
argument_list|()
condition|)
block|{
name|String
name|revision
init|=
name|journal
operator|.
name|next
argument_list|()
operator|.
name|getRevision
argument_list|()
decl_stmt|;
name|checker
operator|.
name|print
argument_list|(
literal|"Checking revision {0}"
argument_list|,
name|revision
argument_list|)
expr_stmt|;
try|try
block|{
name|revisionCount
operator|++
expr_stmt|;
for|for
control|(
name|String
name|path
range|:
name|filterPaths
control|)
block|{
if|if
condition|(
name|pathToValidRevision
operator|.
name|get
argument_list|(
name|path
argument_list|)
operator|==
literal|null
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|corruptPaths
init|=
name|pathToCorruptPaths
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|String
name|corruptPath
init|=
name|checker
operator|.
name|checkPathAtRevision
argument_list|(
name|revision
argument_list|,
name|corruptPaths
argument_list|,
name|path
argument_list|,
name|checkBinaries
argument_list|)
decl_stmt|;
if|if
condition|(
name|corruptPath
operator|==
literal|null
condition|)
block|{
name|checker
operator|.
name|print
argument_list|(
literal|"Path {0} is consistent"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|pathToValidRevision
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|revision
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
else|else
block|{
name|corruptPaths
operator|.
name|add
argument_list|(
name|corruptPath
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|checker
operator|.
name|printError
argument_list|(
literal|"Skipping invalid record id {0}"
argument_list|,
name|revision
argument_list|)
expr_stmt|;
block|}
block|}
name|checker
operator|.
name|print
argument_list|(
literal|"Searched through {0} revisions"
argument_list|,
name|revisionCount
argument_list|)
expr_stmt|;
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
name|checker
operator|.
name|print
argument_list|(
literal|"No good revision found"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|String
name|path
range|:
name|filterPaths
control|)
block|{
name|String
name|validRevision
init|=
name|pathToValidRevision
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|checker
operator|.
name|print
argument_list|(
literal|"Latest good revision for path {0} is {1}"
argument_list|,
name|path
argument_list|,
name|validRevision
operator|!=
literal|null
condition|?
name|validRevision
else|:
literal|"none"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|ioStatistics
condition|)
block|{
name|checker
operator|.
name|print
argument_list|(
literal|"[I/O] Segment read: Number of operations: {0}"
argument_list|,
name|checker
operator|.
name|statisticsIOMonitor
operator|.
name|ioOperations
argument_list|)
expr_stmt|;
name|checker
operator|.
name|print
argument_list|(
literal|"[I/O] Segment read: Total size: {0} ({1} bytes)"
argument_list|,
name|humanReadableByteCount
argument_list|(
name|checker
operator|.
name|statisticsIOMonitor
operator|.
name|readBytes
operator|.
name|get
argument_list|()
argument_list|)
argument_list|,
name|checker
operator|.
name|statisticsIOMonitor
operator|.
name|readBytes
argument_list|)
expr_stmt|;
name|checker
operator|.
name|print
argument_list|(
literal|"[I/O] Segment read: Total time: {0} ns"
argument_list|,
name|checker
operator|.
name|statisticsIOMonitor
operator|.
name|readTime
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Create a new consistency checker instance      *      * @param directory        directory containing the tar files      * @param debugInterval    number of seconds between printing progress information to      *                         the console during the full traversal phase.      * @param ioStatistics     if {@code true} prints I/O statistics gathered while consistency       *                         check was performed      * @param outWriter        text output stream writer      * @param errWriter        text error stream writer                              * @throws IOException      */
specifier|public
name|ConsistencyChecker
parameter_list|(
name|File
name|directory
parameter_list|,
name|long
name|debugInterval
parameter_list|,
name|boolean
name|ioStatistics
parameter_list|,
name|PrintWriter
name|outWriter
parameter_list|,
name|PrintWriter
name|errWriter
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
block|{
name|FileStoreBuilder
name|builder
init|=
name|fileStoreBuilder
argument_list|(
name|directory
argument_list|)
decl_stmt|;
if|if
condition|(
name|ioStatistics
condition|)
block|{
name|builder
operator|.
name|withIOMonitor
argument_list|(
name|statisticsIOMonitor
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|store
operator|=
name|builder
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
name|this
operator|.
name|outWriter
operator|=
name|outWriter
expr_stmt|;
name|this
operator|.
name|errWriter
operator|=
name|errWriter
expr_stmt|;
block|}
comment|/**      * Checks the consistency of the supplied {@code paths} at the given {@code revision},       * starting first with already known {@code corruptPaths}.      *       * @param revision      revision to be checked      * @param corruptPaths  already known corrupt paths from previous revisions      * @param path          path on which to run consistency check,       *                      provided there are no corrupt paths.      * @param checkBinaries if {@code true} full content of binary properties will be scanned      * @return              {@code null}, if the content tree rooted at path is consistent       *                      in this revision or the path of the first inconsistency otherwise.        */
specifier|public
name|String
name|checkPathAtRevision
parameter_list|(
name|String
name|revision
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|corruptPaths
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|checkBinaries
parameter_list|)
block|{
name|String
name|result
init|=
literal|null
decl_stmt|;
name|store
operator|.
name|setRevision
argument_list|(
name|revision
argument_list|)
expr_stmt|;
name|NodeState
name|root
init|=
name|SegmentNodeStoreBuilders
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
for|for
control|(
name|String
name|corruptPath
range|:
name|corruptPaths
control|)
block|{
try|try
block|{
name|NodeWrapper
name|wrapper
init|=
name|NodeWrapper
operator|.
name|deriveTraversableNodeOnPath
argument_list|(
name|root
argument_list|,
name|corruptPath
argument_list|)
decl_stmt|;
name|result
operator|=
name|checkNode
argument_list|(
name|wrapper
operator|.
name|node
argument_list|,
name|wrapper
operator|.
name|path
argument_list|,
name|checkBinaries
argument_list|)
expr_stmt|;
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
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|debug
argument_list|(
literal|"Path {0} not found"
argument_list|,
name|corruptPath
argument_list|)
expr_stmt|;
block|}
block|}
name|nodeCount
operator|=
literal|0
expr_stmt|;
name|propertyCount
operator|=
literal|0
expr_stmt|;
name|print
argument_list|(
literal|"Checking {0}"
argument_list|,
name|path
argument_list|)
expr_stmt|;
try|try
block|{
name|NodeWrapper
name|wrapper
init|=
name|NodeWrapper
operator|.
name|deriveTraversableNodeOnPath
argument_list|(
name|root
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|result
operator|=
name|checkNodeAndDescendants
argument_list|(
name|wrapper
operator|.
name|node
argument_list|,
name|wrapper
operator|.
name|path
argument_list|,
name|checkBinaries
argument_list|)
expr_stmt|;
name|print
argument_list|(
literal|"Checked {0} nodes and {1} properties"
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
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|printError
argument_list|(
literal|"Path {0} not found"
argument_list|,
name|path
argument_list|)
expr_stmt|;
return|return
name|path
return|;
block|}
block|}
comment|/**      * Checks the consistency of a node and its properties at the given path.      *       * @param node              node to be checked      * @param path              path of the node      * @param checkBinaries     if {@code true} full content of binary properties will be scanned      * @return                  {@code null}, if the node is consistent,       *                          or the path of the first inconsistency otherwise.      */
specifier|private
name|String
name|checkNode
parameter_list|(
name|NodeState
name|node
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|checkBinaries
parameter_list|)
block|{
try|try
block|{
name|debug
argument_list|(
literal|"Traversing {0}"
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
literal|"Checking {0}/{1}"
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
name|checkBinaries
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
name|checkBinaries
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|propertyCount
operator|++
expr_stmt|;
name|propertyState
operator|.
name|getValue
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|printError
argument_list|(
literal|"Error while traversing {0}: {1}"
argument_list|,
name|path
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|path
return|;
block|}
block|}
comment|/**      * Recursively checks the consistency of a node and its descendants at the given path.      * @param node          node to be checked      * @param path          path of the node      * @param checkBinaries if {@code true} full content of binary properties will be scanned      * @return              {@code null}, if the node is consistent,       *                      or the path of the first inconsistency otherwise.      */
specifier|private
name|String
name|checkNodeAndDescendants
parameter_list|(
name|NodeState
name|node
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|checkBinaries
parameter_list|)
block|{
name|String
name|result
init|=
name|checkNode
argument_list|(
name|node
argument_list|,
name|path
argument_list|,
name|checkBinaries
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
try|try
block|{
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
name|result
operator|=
name|checkNodeAndDescendants
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
name|checkBinaries
argument_list|)
expr_stmt|;
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
name|printError
argument_list|(
literal|"Error while traversing {0}: {1}"
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
specifier|static
class|class
name|NodeWrapper
block|{
name|NodeState
name|node
decl_stmt|;
name|String
name|path
decl_stmt|;
name|NodeWrapper
parameter_list|(
name|NodeState
name|node
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
specifier|static
name|NodeWrapper
name|deriveTraversableNodeOnPath
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|String
name|path
parameter_list|)
block|{
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
condition|)
block|{
if|if
condition|(
operator|!
name|parent
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid path: "
operator|+
name|path
argument_list|)
throw|;
block|}
return|return
operator|new
name|NodeWrapper
argument_list|(
name|parent
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|,
name|path
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|NodeWrapper
argument_list|(
name|parent
argument_list|,
name|parentPath
argument_list|)
return|;
block|}
block|}
block|}
specifier|private
name|void
name|traverse
parameter_list|(
name|Blob
name|blob
parameter_list|,
name|boolean
name|checkBinaries
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|checkBinaries
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
name|buffer
operator|.
name|length
argument_list|)
decl_stmt|;
while|while
condition|(
name|l
operator|>=
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
name|buffer
operator|.
name|length
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
name|propertyCount
operator|++
expr_stmt|;
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
name|Override
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
name|void
name|print
parameter_list|(
name|String
name|format
parameter_list|)
block|{
name|outWriter
operator|.
name|println
argument_list|(
name|format
argument_list|)
expr_stmt|;
block|}
specifier|private
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
name|outWriter
operator|.
name|println
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
name|format
argument_list|,
name|arg
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
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
name|outWriter
operator|.
name|println
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
name|format
argument_list|,
name|arg1
argument_list|,
name|arg2
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|printError
parameter_list|(
name|String
name|format
parameter_list|,
name|Object
name|arg
parameter_list|)
block|{
name|errWriter
operator|.
name|println
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
name|format
argument_list|,
name|arg
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|printError
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
name|errWriter
operator|.
name|println
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
name|format
argument_list|,
name|arg1
argument_list|,
name|arg2
argument_list|)
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
name|print
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
name|print
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

