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
name|index
operator|.
name|inventory
package|;
end_package

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
name|util
operator|.
name|Calendar
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Strings
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
name|ArrayListMultimap
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
name|ImmutableList
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
name|ListMultimap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|inventory
operator|.
name|Format
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|inventory
operator|.
name|InventoryPrinter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Property
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Reference
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Service
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
name|jmx
operator|.
name|IndexStatsMBean
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
name|IOUtils
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
name|AsyncIndexInfo
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
name|AsyncIndexInfoService
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
name|IndexInfo
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
name|IndexInfoService
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
name|util
operator|.
name|ISO8601
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

begin_class
annotation|@
name|Component
annotation|@
name|Service
annotation|@
name|Properties
argument_list|(
block|{
annotation|@
name|Property
argument_list|(
name|name
operator|=
literal|"felix.inventory.printer.name"
argument_list|,
name|value
operator|=
literal|"oak-index-stats"
argument_list|)
block|,
annotation|@
name|Property
argument_list|(
name|name
operator|=
literal|"felix.inventory.printer.title"
argument_list|,
name|value
operator|=
literal|"Oak Index Stats"
argument_list|)
block|,
annotation|@
name|Property
argument_list|(
name|name
operator|=
literal|"felix.inventory.printer.format"
argument_list|,
name|value
operator|=
block|{
literal|"TEXT"
block|}
argument_list|)
block|}
argument_list|)
specifier|public
class|class
name|IndexPrinter
implements|implements
name|InventoryPrinter
block|{
annotation|@
name|Reference
specifier|private
name|IndexInfoService
name|indexInfoService
decl_stmt|;
annotation|@
name|Reference
specifier|private
name|AsyncIndexInfoService
name|asyncIndexInfoService
decl_stmt|;
specifier|public
name|IndexPrinter
parameter_list|()
block|{     }
specifier|public
name|IndexPrinter
parameter_list|(
name|IndexInfoService
name|indexInfoService
parameter_list|,
name|AsyncIndexInfoService
name|asyncIndexInfoService
parameter_list|)
block|{
name|this
operator|.
name|indexInfoService
operator|=
name|checkNotNull
argument_list|(
name|indexInfoService
argument_list|)
expr_stmt|;
name|this
operator|.
name|asyncIndexInfoService
operator|=
name|checkNotNull
argument_list|(
name|asyncIndexInfoService
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|print
parameter_list|(
name|PrintWriter
name|pw
parameter_list|,
name|Format
name|format
parameter_list|,
name|boolean
name|isZip
parameter_list|)
block|{
comment|//TODO Highlight if failing
name|printAsyncIndexInfo
argument_list|(
name|pw
argument_list|)
expr_stmt|;
name|printIndexInfo
argument_list|(
name|pw
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|printAsyncIndexInfo
parameter_list|(
name|PrintWriter
name|pw
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|asyncLanes
init|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|asyncIndexInfoService
operator|.
name|getAsyncLanes
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|title
init|=
literal|"Async Indexers State"
decl_stmt|;
name|printTitle
argument_list|(
name|pw
argument_list|,
name|title
argument_list|)
expr_stmt|;
name|pw
operator|.
name|printf
argument_list|(
literal|"Number of async indexer lanes : %d%n"
argument_list|,
name|asyncLanes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|lane
range|:
name|asyncLanes
control|)
block|{
name|pw
operator|.
name|println
argument_list|(
name|lane
argument_list|)
expr_stmt|;
name|AsyncIndexInfo
name|info
init|=
name|asyncIndexInfoService
operator|.
name|getInfo
argument_list|(
name|lane
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|pw
operator|.
name|printf
argument_list|(
literal|"    Last Indexed To      : %s%n"
argument_list|,
name|formatTime
argument_list|(
name|info
operator|.
name|getLastIndexedTo
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|IndexStatsMBean
name|stats
init|=
name|info
operator|.
name|getStatsMBean
argument_list|()
decl_stmt|;
if|if
condition|(
name|stats
operator|!=
literal|null
condition|)
block|{
name|pw
operator|.
name|printf
argument_list|(
literal|"    Status              : %s%n"
argument_list|,
name|stats
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|pw
operator|.
name|printf
argument_list|(
literal|"    Failing             : %s%n"
argument_list|,
name|stats
operator|.
name|isFailing
argument_list|()
argument_list|)
expr_stmt|;
name|pw
operator|.
name|printf
argument_list|(
literal|"    Paused              : %s%n"
argument_list|,
name|stats
operator|.
name|isPaused
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|stats
operator|.
name|isFailing
argument_list|()
condition|)
block|{
name|pw
operator|.
name|printf
argument_list|(
literal|"    Failing Since       : %s%n"
argument_list|,
name|stats
operator|.
name|getFailingSince
argument_list|()
argument_list|)
expr_stmt|;
name|pw
operator|.
name|printf
argument_list|(
literal|"    Latest Error        : %s%n"
argument_list|,
name|stats
operator|.
name|getLatestError
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|pw
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|void
name|printTitle
parameter_list|(
name|PrintWriter
name|pw
parameter_list|,
name|String
name|title
parameter_list|)
block|{
name|pw
operator|.
name|println
argument_list|(
name|title
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
name|Strings
operator|.
name|repeat
argument_list|(
literal|"="
argument_list|,
name|title
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|printIndexInfo
parameter_list|(
name|PrintWriter
name|pw
parameter_list|)
block|{
name|ListMultimap
argument_list|<
name|String
argument_list|,
name|IndexInfo
argument_list|>
name|infos
init|=
name|ArrayListMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexInfo
name|info
range|:
name|indexInfoService
operator|.
name|getAllIndexInfo
argument_list|()
control|)
block|{
name|infos
operator|.
name|put
argument_list|(
name|info
operator|.
name|getType
argument_list|()
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
name|pw
operator|.
name|printf
argument_list|(
literal|"Total number of indexes : %d%n"
argument_list|,
name|infos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|type
range|:
name|infos
operator|.
name|keySet
argument_list|()
control|)
block|{
name|List
argument_list|<
name|IndexInfo
argument_list|>
name|typedInfo
init|=
name|infos
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
name|String
name|title
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%s(%d)"
argument_list|,
name|type
argument_list|,
name|typedInfo
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|printTitle
argument_list|(
name|pw
argument_list|,
name|title
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|()
expr_stmt|;
for|for
control|(
name|IndexInfo
name|info
range|:
name|typedInfo
control|)
block|{
name|printIndexInfo
argument_list|(
name|pw
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|void
name|printIndexInfo
parameter_list|(
name|PrintWriter
name|pw
parameter_list|,
name|IndexInfo
name|info
parameter_list|)
block|{
name|pw
operator|.
name|println
argument_list|(
name|info
operator|.
name|getIndexPath
argument_list|()
argument_list|)
expr_stmt|;
name|pw
operator|.
name|printf
argument_list|(
literal|"    Type                    : %s%n"
argument_list|,
name|info
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|.
name|getAsyncLaneName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|pw
operator|.
name|printf
argument_list|(
literal|"    async                   : true%n"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|printf
argument_list|(
literal|"    async name              : %s%n"
argument_list|,
name|info
operator|.
name|getAsyncLaneName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|.
name|getIndexedUpToTime
argument_list|()
operator|>
literal|0
condition|)
block|{
name|pw
operator|.
name|printf
argument_list|(
literal|"    Last Indexed Upto       : %s%n"
argument_list|,
name|formatTime
argument_list|(
name|info
operator|.
name|getIndexedUpToTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|.
name|getLastUpdatedTime
argument_list|()
operator|>
literal|0
condition|)
block|{
name|pw
operator|.
name|printf
argument_list|(
literal|"    Last updated time       : %s%n"
argument_list|,
name|formatTime
argument_list|(
name|info
operator|.
name|getLastUpdatedTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|.
name|getSizeInBytes
argument_list|()
operator|>=
literal|0
condition|)
block|{
name|pw
operator|.
name|printf
argument_list|(
literal|"    Size                    : %s%n"
argument_list|,
name|IOUtils
operator|.
name|humanReadableByteCount
argument_list|(
name|info
operator|.
name|getSizeInBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|.
name|getEstimatedEntryCount
argument_list|()
operator|>=
literal|0
condition|)
block|{
name|pw
operator|.
name|printf
argument_list|(
literal|"    Estimated entry count   : %d%n"
argument_list|,
name|info
operator|.
name|getEstimatedEntryCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|.
name|hasIndexDefinitionChangedWithoutReindexing
argument_list|()
condition|)
block|{
name|pw
operator|.
name|println
argument_list|(
literal|"    Index definition changed without reindexing"
argument_list|)
expr_stmt|;
name|String
name|diff
init|=
name|info
operator|.
name|getIndexDefinitionDiff
argument_list|()
decl_stmt|;
if|if
condition|(
name|diff
operator|!=
literal|null
condition|)
block|{
name|pw
operator|.
name|println
argument_list|(
literal|"    "
operator|+
name|diff
argument_list|)
expr_stmt|;
block|}
block|}
name|pw
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|String
name|formatTime
parameter_list|(
name|long
name|time
parameter_list|)
block|{
name|Calendar
name|cal
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|cal
operator|.
name|setTimeInMillis
argument_list|(
name|time
argument_list|)
expr_stmt|;
return|return
name|ISO8601
operator|.
name|format
argument_list|(
name|cal
argument_list|)
return|;
block|}
block|}
end_class

end_unit

