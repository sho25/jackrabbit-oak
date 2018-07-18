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
name|spi
operator|.
name|persistence
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

begin_comment
comment|/**  * SegmentArchiveManager provides a low-level access to the segment files (eg.  * stored in the .tar). It allows to perform a few FS-like operations (delete,  * rename, copy, etc.) and also opens the segment archives either for reading  * or reading and writing.  *<p>  * The implementation doesn't need to be thread-safe.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SegmentArchiveManager
block|{
comment|/**      * List names of the available .tar archives.      *      * @return archive list      */
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
function_decl|;
comment|/**      * Opens a given archive for reading.      *      * @param archiveName      * @return the archive reader or null if the archive doesn't exist      */
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
function_decl|;
comment|/**      * Creates a new archive.      *      * @param archiveName      * @return the archive writer      */
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
function_decl|;
comment|/**      * Deletes the archive if exists.      *      * @param archiveName      * @return true if the archive was removed, false otherwise      */
name|boolean
name|delete
parameter_list|(
annotation|@
name|NotNull
name|String
name|archiveName
parameter_list|)
function_decl|;
comment|/**      * Renames the archive.      *      * @param from the existing archive      * @param to new name      * @return true if the archive was renamed, false otherwise      */
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
function_decl|;
comment|/**      * Copies the archive with all the segments.      *      * @param from the existing archive      * @param to new name      */
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
function_decl|;
comment|/**      * Check if archive exists.      *      * @param archiveName archive to check      * @return true if archive exists, false otherwise      */
name|boolean
name|exists
parameter_list|(
annotation|@
name|NotNull
name|String
name|archiveName
parameter_list|)
function_decl|;
comment|/**      * Finds all the segments included in the archive.      *      * @param archiveName archive to recover      * @param entries results will be put there, in the order of presence in the      *                archive      */
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
function_decl|;
block|}
end_interface

end_unit

