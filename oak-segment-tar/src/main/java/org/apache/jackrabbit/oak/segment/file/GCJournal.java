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
name|Charsets
operator|.
name|UTF_8
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
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
operator|.
name|newBufferedWriter
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
operator|.
name|readAllLines
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|StandardOpenOption
operator|.
name|APPEND
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|StandardOpenOption
operator|.
name|CREATE
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|StandardOpenOption
operator|.
name|DSYNC
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|StandardOpenOption
operator|.
name|WRITE
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
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
name|nio
operator|.
name|file
operator|.
name|Path
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
name|Collection
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
comment|/**  * Persists the repository size and the reclaimed size following a cleanup  * operation in the {@link #GC_JOURNAL gc journal} file with the format:  * 'repoSize, reclaimedSize, timestamp, gcGen, nodes compacted'.  */
end_comment

begin_class
specifier|public
class|class
name|GCJournal
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
name|GCJournal
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|GC_JOURNAL
init|=
literal|"gc.log"
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|File
name|directory
decl_stmt|;
specifier|private
name|GCJournalEntry
name|latest
decl_stmt|;
specifier|public
name|GCJournal
parameter_list|(
annotation|@
name|Nonnull
name|File
name|directory
parameter_list|)
block|{
name|this
operator|.
name|directory
operator|=
name|checkNotNull
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
comment|/**      * Persists the repository stats (current size, reclaimed size, gc      * generation, number of compacted nodes) following a cleanup operation for      * a successful compaction. NOOP if the gcGeneration is the same as the one      * persisted previously.      *      * @param reclaimedSize size reclaimed by cleanup      * @param repoSize current repo size      * @param gcGeneration gc generation      * @param nodes number of compacted nodes      */
specifier|public
specifier|synchronized
name|void
name|persist
parameter_list|(
name|long
name|reclaimedSize
parameter_list|,
name|long
name|repoSize
parameter_list|,
name|int
name|gcGeneration
parameter_list|,
name|long
name|nodes
parameter_list|)
block|{
name|GCJournalEntry
name|current
init|=
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|current
operator|.
name|getGcGeneration
argument_list|()
operator|==
name|gcGeneration
condition|)
block|{
comment|// failed compaction, only update the journal if the generation
comment|// increases
return|return;
block|}
name|latest
operator|=
operator|new
name|GCJournalEntry
argument_list|(
name|repoSize
argument_list|,
name|reclaimedSize
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
name|gcGeneration
argument_list|,
name|nodes
argument_list|)
expr_stmt|;
name|Path
name|path
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|GC_JOURNAL
argument_list|)
operator|.
name|toPath
argument_list|()
decl_stmt|;
try|try
block|{
try|try
init|(
name|BufferedWriter
name|w
init|=
name|newBufferedWriter
argument_list|(
name|path
argument_list|,
name|UTF_8
argument_list|,
name|WRITE
argument_list|,
name|APPEND
argument_list|,
name|CREATE
argument_list|,
name|DSYNC
argument_list|)
init|)
block|{
name|w
operator|.
name|write
argument_list|(
name|latest
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|newLine
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error writing gc journal"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Returns the latest entry available      */
specifier|public
specifier|synchronized
name|GCJournalEntry
name|read
parameter_list|()
block|{
if|if
condition|(
name|latest
operator|==
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|all
init|=
name|readLines
argument_list|()
decl_stmt|;
if|if
condition|(
name|all
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|latest
operator|=
name|GCJournalEntry
operator|.
name|EMPTY
expr_stmt|;
block|}
else|else
block|{
name|String
name|info
init|=
name|all
operator|.
name|get
argument_list|(
name|all
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|latest
operator|=
name|GCJournalEntry
operator|.
name|fromString
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|latest
return|;
block|}
comment|/**      * Returns all available entries from the journal      */
specifier|public
specifier|synchronized
name|Collection
argument_list|<
name|GCJournalEntry
argument_list|>
name|readAll
parameter_list|()
block|{
name|List
argument_list|<
name|GCJournalEntry
argument_list|>
name|all
init|=
operator|new
name|ArrayList
argument_list|<
name|GCJournalEntry
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|l
range|:
name|readLines
argument_list|()
control|)
block|{
name|all
operator|.
name|add
argument_list|(
name|GCJournalEntry
operator|.
name|fromString
argument_list|(
name|l
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|all
return|;
block|}
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|readLines
parameter_list|()
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|GC_JOURNAL
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
try|try
block|{
return|return
name|readAllLines
argument_list|(
name|file
operator|.
name|toPath
argument_list|()
argument_list|,
name|UTF_8
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error reading gc journal"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
return|;
block|}
specifier|static
class|class
name|GCJournalEntry
block|{
specifier|static
name|GCJournalEntry
name|EMPTY
init|=
operator|new
name|GCJournalEntry
argument_list|(
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|long
name|repoSize
decl_stmt|;
specifier|private
specifier|final
name|long
name|reclaimedSize
decl_stmt|;
specifier|private
specifier|final
name|long
name|ts
decl_stmt|;
specifier|private
specifier|final
name|int
name|gcGeneration
decl_stmt|;
specifier|private
specifier|final
name|long
name|nodes
decl_stmt|;
specifier|public
name|GCJournalEntry
parameter_list|(
name|long
name|repoSize
parameter_list|,
name|long
name|reclaimedSize
parameter_list|,
name|long
name|ts
parameter_list|,
name|int
name|gcGeneration
parameter_list|,
name|long
name|nodes
parameter_list|)
block|{
name|this
operator|.
name|repoSize
operator|=
name|repoSize
expr_stmt|;
name|this
operator|.
name|reclaimedSize
operator|=
name|reclaimedSize
expr_stmt|;
name|this
operator|.
name|ts
operator|=
name|ts
expr_stmt|;
name|this
operator|.
name|gcGeneration
operator|=
name|gcGeneration
expr_stmt|;
name|this
operator|.
name|nodes
operator|=
name|nodes
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|repoSize
operator|+
literal|","
operator|+
name|reclaimedSize
operator|+
literal|","
operator|+
name|ts
operator|+
literal|","
operator|+
name|gcGeneration
operator|+
literal|","
operator|+
name|nodes
return|;
block|}
specifier|static
name|GCJournalEntry
name|fromString
parameter_list|(
name|String
name|in
parameter_list|)
block|{
name|String
index|[]
name|items
init|=
name|in
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|long
name|repoSize
init|=
name|safeParse
argument_list|(
name|items
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|long
name|reclaimedSize
init|=
name|safeParse
argument_list|(
name|items
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|long
name|ts
init|=
name|safeParse
argument_list|(
name|items
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|int
name|gcGen
init|=
operator|(
name|int
operator|)
name|safeParse
argument_list|(
name|items
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|long
name|nodes
init|=
name|safeParse
argument_list|(
name|items
argument_list|,
literal|4
argument_list|)
decl_stmt|;
return|return
operator|new
name|GCJournalEntry
argument_list|(
name|repoSize
argument_list|,
name|reclaimedSize
argument_list|,
name|ts
argument_list|,
name|gcGen
argument_list|,
name|nodes
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|long
name|safeParse
parameter_list|(
name|String
index|[]
name|items
parameter_list|,
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
name|items
operator|.
name|length
operator|<
name|index
operator|-
literal|1
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|String
name|in
init|=
name|items
index|[
name|index
index|]
decl_stmt|;
try|try
block|{
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|in
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to parse {} as long value."
argument_list|,
name|in
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
return|return
operator|-
literal|1
return|;
block|}
comment|/**          * Returns the repository size          */
specifier|public
name|long
name|getRepoSize
parameter_list|()
block|{
return|return
name|repoSize
return|;
block|}
comment|/**          * Returns the reclaimed size          */
specifier|public
name|long
name|getReclaimedSize
parameter_list|()
block|{
return|return
name|reclaimedSize
return|;
block|}
comment|/**          * Returns the timestamp          */
specifier|public
name|long
name|getTs
parameter_list|()
block|{
return|return
name|ts
return|;
block|}
comment|/**          * Returns the gc generation          */
specifier|public
name|int
name|getGcGeneration
parameter_list|()
block|{
return|return
name|gcGeneration
return|;
block|}
comment|/**          * Returns the number of compacted nodes          */
specifier|public
name|long
name|getNodes
parameter_list|()
block|{
return|return
name|nodes
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|gcGeneration
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|nodes
operator|^
operator|(
name|nodes
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|reclaimedSize
operator|^
operator|(
name|reclaimedSize
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|repoSize
operator|^
operator|(
name|repoSize
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|ts
operator|^
operator|(
name|ts
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|GCJournalEntry
name|other
init|=
operator|(
name|GCJournalEntry
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|gcGeneration
operator|!=
name|other
operator|.
name|gcGeneration
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|nodes
operator|!=
name|other
operator|.
name|nodes
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|reclaimedSize
operator|!=
name|other
operator|.
name|reclaimedSize
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|repoSize
operator|!=
name|other
operator|.
name|repoSize
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|ts
operator|!=
name|other
operator|.
name|ts
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
block|}
end_class

end_unit

