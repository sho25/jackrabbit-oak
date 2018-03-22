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

begin_comment
comment|/**  * The journal is a special, atomically updated file that records the state of  * the repository as a sequence of references to successive root node records.  * See<a href="https://jackrabbit.apache.org/oak/docs/nodestore/segment/overview.html">  * oak-segment-tar</a> documentation for more details.  */
end_comment

begin_interface
specifier|public
interface|interface
name|JournalFile
block|{
comment|/**      * Opens the journal file for reading. The returned object will represent      * the current state of the journal. Subsequent writes made by the      * {@link JournalFileWriter} won't be visible until a new      * {@link JournalFileReader} is opened.      *      * @return the reader representing the current state of the journal      * @throws IOException      */
name|JournalFileReader
name|openJournalReader
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Opens the journal file for writing.      * @return      * @throws IOException      */
name|JournalFileWriter
name|openJournalWriter
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Return the name representing the journal file.      * @return name (eg. file name) representing the journal      */
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * Check if the journal already exists.      * @return {@code true} if the journal has been already created by the      * {@link JournalFileWriter}      */
name|boolean
name|exists
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

