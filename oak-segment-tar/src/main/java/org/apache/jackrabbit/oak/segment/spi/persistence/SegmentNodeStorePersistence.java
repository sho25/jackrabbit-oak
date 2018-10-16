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
name|monitor
operator|.
name|FileStoreMonitor
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
name|monitor
operator|.
name|IOMonitor
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

begin_comment
comment|/**  * This type is a main entry point for the segment node store persistence. It's  * used every time the access to the underlying storage (eg. tar files) is required.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SegmentNodeStorePersistence
block|{
comment|/**      * Opens a new archive manager. It'll be used to access the archives containing      * segments.      *      * @param memoryMapping whether the memory mapping should be used (if the given      *                      persistence supports it)      * @param offHeapAccess whether off heap access for segements should be used      * @param ioMonitor object used to monitor segment-related IO access. The      *                  implementation should call the appropriate methods when      *                  accessing segments.      * @param fileStoreMonitor object used to monitor the general IO usage.      * @return segment archive manager      * @throws IOException      */
name|SegmentArchiveManager
name|createArchiveManager
parameter_list|(
name|boolean
name|memoryMapping
parameter_list|,
name|boolean
name|offHeapAccess
parameter_list|,
name|IOMonitor
name|ioMonitor
parameter_list|,
name|FileStoreMonitor
name|fileStoreMonitor
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Check if the segment store already contains any segments      * @return {@code true} is some segments are available for reading      */
name|boolean
name|segmentFilesExist
parameter_list|()
function_decl|;
comment|/**      * Create the {@link JournalFile}.      * @return object representing the segment journal file      */
name|JournalFile
name|getJournalFile
parameter_list|()
function_decl|;
comment|/**      * Create the {@link GCJournalFile}.      * @return object representing the GC journal file      * @throws IOException      */
name|GCJournalFile
name|getGCJournalFile
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Create the {@link ManifestFile}.      * @return object representing the manifest file      * @throws IOException      */
name|ManifestFile
name|getManifestFile
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Acquire the lock on the repository. During the lock lifetime it shouldn't      * be possible to acquire it again, either by a local or by a remote process.      *<p>      * The lock can be released manually by calling {@link RepositoryLock#unlock()}.      * If the segment node store is shut down uncleanly (eg. the process crashes),      * it should be released automatically, so no extra maintenance tasks are      * required to run the process again.      * @return the acquired repository lock      * @throws IOException      */
name|RepositoryLock
name|lockRepository
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

