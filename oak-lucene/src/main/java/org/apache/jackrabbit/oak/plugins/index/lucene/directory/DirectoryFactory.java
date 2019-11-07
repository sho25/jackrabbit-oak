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
name|lucene
operator|.
name|directory
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
name|LuceneIndexDefinition
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
name|NodeBuilder
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

begin_comment
comment|/**  * A builder for Lucene directories.  */
end_comment

begin_interface
specifier|public
interface|interface
name|DirectoryFactory
block|{
comment|/**      * Open a new directory.      *       * Internally, it read the data from the index definition. It writes to the      * builder, for example when closing the directory.      *       * @param definition the index definition      * @param builder the builder pointing to the index definition (see above      *            for usage)      * @param dirName the name of the directory (in the file system)      * @param reindex whether reindex is needed      * @return the Lucene directory      */
name|Directory
name|newInstance
parameter_list|(
name|LuceneIndexDefinition
name|definition
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|dirName
parameter_list|,
name|boolean
name|reindex
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Determines if the Directory is having a remote storage      * or local storage      */
name|boolean
name|remoteDirectory
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

