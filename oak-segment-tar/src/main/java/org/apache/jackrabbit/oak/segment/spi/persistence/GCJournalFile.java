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
name|List
import|;
end_import

begin_comment
comment|/**  * This type abstracts the {@code gc.log} file, used to save information about  * the segment garbage collection. Each record is represented by a single string.  *<br><br>  * The implementation<b>doesn't need to be</b> thread-safe.  */
end_comment

begin_interface
specifier|public
interface|interface
name|GCJournalFile
block|{
comment|/**      * Write the new line to the GC journal file.      *      * @param line the line to write. It should contain neither special characters      *             nor the newline {@code \n}.      * @throws IOException      */
name|void
name|writeLine
parameter_list|(
name|String
name|line
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Return the list of all written records in the same order as they were      * written.      *      * @return the list of all written lines      * @throws IOException      */
name|List
argument_list|<
name|String
argument_list|>
name|readLines
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Removes the content of the gc.log      */
name|void
name|truncate
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

