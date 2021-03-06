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
name|segment
operator|.
name|spi
operator|.
name|persistence
operator|.
name|split
package|;
end_package

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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|UUID
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
name|spi
operator|.
name|persistence
operator|.
name|SegmentArchiveManager
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
name|spi
operator|.
name|persistence
operator|.
name|SegmentArchiveReader
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
name|spi
operator|.
name|persistence
operator|.
name|SegmentArchiveWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_class
specifier|public
class|class
name|SplitSegmentArchiveManager
implements|implements
name|SegmentArchiveManager
block|{
specifier|private
specifier|final
name|SegmentArchiveManager
name|roArchiveManager
decl_stmt|;
specifier|private
specifier|final
name|SegmentArchiveManager
name|rwArchiveManager
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|roArchiveList
decl_stmt|;
specifier|public
name|SplitSegmentArchiveManager
parameter_list|(
name|SegmentArchiveManager
name|roArchiveManager
parameter_list|,
name|SegmentArchiveManager
name|rwArchiveManager
parameter_list|,
name|String
name|lastRoArchive
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|roArchiveManager
operator|=
name|roArchiveManager
expr_stmt|;
name|this
operator|.
name|rwArchiveManager
operator|=
name|rwArchiveManager
expr_stmt|;
name|this
operator|.
name|roArchiveList
operator|=
name|getRoArchives
argument_list|(
name|lastRoArchive
argument_list|)
expr_stmt|;
block|}
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|getRoArchives
parameter_list|(
name|String
name|lastRoArchive
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|archives
init|=
name|roArchiveManager
operator|.
name|listArchives
argument_list|()
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|archives
argument_list|)
expr_stmt|;
name|int
name|index
init|=
name|archives
operator|.
name|indexOf
argument_list|(
name|lastRoArchive
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Can't find archive "
operator|+
name|lastRoArchive
operator|+
literal|" in the read-only persistence"
argument_list|)
throw|;
block|}
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|archives
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|index
operator|+
literal|1
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|NotNull
name|List
argument_list|<
name|String
argument_list|>
name|listArchives
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|roArchiveList
argument_list|)
expr_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|rwArchiveManager
operator|.
name|listArchives
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|SegmentArchiveReader
name|open
parameter_list|(
annotation|@
name|NotNull
name|String
name|archiveName
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|roArchiveList
operator|.
name|contains
argument_list|(
name|archiveName
argument_list|)
condition|)
block|{
name|SegmentArchiveReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
name|roArchiveManager
operator|.
name|open
argument_list|(
name|archiveName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
block|{
name|reader
operator|=
name|roArchiveManager
operator|.
name|forceOpen
argument_list|(
name|archiveName
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|UnclosedSegmentArchiveReader
argument_list|(
name|reader
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|rwArchiveManager
operator|.
name|open
argument_list|(
name|archiveName
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|SegmentArchiveReader
name|forceOpen
parameter_list|(
name|String
name|archiveName
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|roArchiveList
operator|.
name|contains
argument_list|(
name|archiveName
argument_list|)
condition|)
block|{
return|return
name|roArchiveManager
operator|.
name|forceOpen
argument_list|(
name|archiveName
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|rwArchiveManager
operator|.
name|forceOpen
argument_list|(
name|archiveName
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|NotNull
name|SegmentArchiveWriter
name|create
parameter_list|(
annotation|@
name|NotNull
name|String
name|archiveName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|rwArchiveManager
operator|.
name|create
argument_list|(
name|archiveName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|delete
parameter_list|(
annotation|@
name|NotNull
name|String
name|archiveName
parameter_list|)
block|{
if|if
condition|(
name|roArchiveList
operator|.
name|contains
argument_list|(
name|archiveName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|rwArchiveManager
operator|.
name|delete
argument_list|(
name|archiveName
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|renameTo
parameter_list|(
annotation|@
name|NotNull
name|String
name|from
parameter_list|,
annotation|@
name|NotNull
name|String
name|to
parameter_list|)
block|{
if|if
condition|(
name|roArchiveList
operator|.
name|contains
argument_list|(
name|from
argument_list|)
operator|||
name|roArchiveList
operator|.
name|contains
argument_list|(
name|to
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|rwArchiveManager
operator|.
name|renameTo
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|copyFile
parameter_list|(
annotation|@
name|NotNull
name|String
name|from
parameter_list|,
annotation|@
name|NotNull
name|String
name|to
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|roArchiveList
operator|.
name|contains
argument_list|(
name|to
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't overwrite the read-only "
operator|+
name|to
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|roArchiveList
operator|.
name|contains
argument_list|(
name|from
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't copy the archive between persistence "
operator|+
name|from
operator|+
literal|" -> "
operator|+
name|to
argument_list|)
throw|;
block|}
else|else
block|{
name|rwArchiveManager
operator|.
name|copyFile
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|(
annotation|@
name|NotNull
name|String
name|archiveName
parameter_list|)
block|{
return|return
name|roArchiveList
operator|.
name|contains
argument_list|(
name|archiveName
argument_list|)
operator|||
name|rwArchiveManager
operator|.
name|exists
argument_list|(
name|archiveName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|recoverEntries
parameter_list|(
annotation|@
name|NotNull
name|String
name|archiveName
parameter_list|,
annotation|@
name|NotNull
name|LinkedHashMap
argument_list|<
name|UUID
argument_list|,
name|byte
index|[]
argument_list|>
name|entries
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|roArchiveList
operator|.
name|contains
argument_list|(
name|archiveName
argument_list|)
condition|)
block|{
name|roArchiveManager
operator|.
name|recoverEntries
argument_list|(
name|archiveName
argument_list|,
name|entries
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rwArchiveManager
operator|.
name|recoverEntries
argument_list|(
name|archiveName
argument_list|,
name|entries
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

