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
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularData
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
name|Description
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
name|Name
import|;
end_import

begin_interface
specifier|public
interface|interface
name|LuceneIndexMBean
block|{
name|String
name|TYPE
init|=
literal|"LuceneIndex"
decl_stmt|;
name|TabularData
name|getIndexStats
parameter_list|()
throws|throws
name|IOException
function_decl|;
name|TabularData
name|getBadIndexStats
parameter_list|()
function_decl|;
name|TabularData
name|getBadPersistedIndexStats
parameter_list|()
function_decl|;
name|boolean
name|isFailing
parameter_list|()
function_decl|;
annotation|@
name|Description
argument_list|(
literal|"Determines the set of index paths upto given maxLevel. This can be used to determine the value for"
operator|+
literal|"[includedPaths]. For this to work you should have [evaluatePathRestrictions] set to true in your index "
operator|+
literal|"definition"
argument_list|)
name|String
index|[]
name|getIndexedPaths
parameter_list|(
annotation|@
name|Description
argument_list|(
literal|"Index path for which stats are to be determined"
argument_list|)
annotation|@
name|Name
argument_list|(
literal|"indexPath"
argument_list|)
name|String
name|indexPath
parameter_list|,
annotation|@
name|Name
argument_list|(
literal|"maxLevel"
argument_list|)
annotation|@
name|Description
argument_list|(
literal|"Maximum depth to examine. E.g. 5. Stats calculation would "
operator|+
literal|"break out after this limit"
argument_list|)
name|int
name|maxLevel
parameter_list|,
annotation|@
name|Description
argument_list|(
literal|"Maximum number of unique paths to examine. E.g. 100. Stats "
operator|+
literal|"calculation would break out after this limit"
argument_list|)
annotation|@
name|Name
argument_list|(
literal|"maxPathCount"
argument_list|)
name|int
name|maxPathCount
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Description
argument_list|(
literal|"Retrieves the fields, and number of documents for each field, for an index. "
operator|+
literal|"This allows to investigate what is stored in the index."
argument_list|)
name|String
index|[]
name|getFieldInfo
parameter_list|(
annotation|@
name|Name
argument_list|(
literal|"indexPath"
argument_list|)
annotation|@
name|Description
argument_list|(
literal|"The index path (empty for all indexes)"
argument_list|)
name|String
name|indexPath
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Description
argument_list|(
literal|"Returns the stored index definition for index at given path in string form"
argument_list|)
name|String
name|getStoredIndexDefinition
parameter_list|(
annotation|@
name|Name
argument_list|(
literal|"indexPath"
argument_list|)
name|String
name|indexPath
parameter_list|)
function_decl|;
annotation|@
name|Description
argument_list|(
literal|"Returns the diff of index definition for index at given path from the stored index definition in "
operator|+
literal|"string form"
argument_list|)
name|String
name|diffStoredIndexDefinition
parameter_list|(
annotation|@
name|Name
argument_list|(
literal|"indexPath"
argument_list|)
name|String
name|indexPath
parameter_list|)
function_decl|;
annotation|@
name|Description
argument_list|(
literal|"Performs consistency check on given index"
argument_list|)
name|String
name|checkConsistency
parameter_list|(
annotation|@
name|Name
argument_list|(
literal|"indexPath"
argument_list|)
name|String
name|indexPath
parameter_list|,
annotation|@
name|Name
argument_list|(
literal|"fullCheck"
argument_list|)
annotation|@
name|Description
argument_list|(
literal|"If set to true a full check would be performed which can be slow as "
operator|+
literal|"it reads all index files. If set to false a quick check is performed to "
operator|+
literal|"check if all blobs referred in index files are present in BlobStore"
argument_list|)
name|boolean
name|fullCheck
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Description
argument_list|(
literal|"Performs consistency check for all Lucene indexes and reports in simple format"
argument_list|)
name|String
index|[]
name|checkAndReportConsistencyOfAllIndexes
parameter_list|(
annotation|@
name|Name
argument_list|(
literal|"fullCheck"
argument_list|)
annotation|@
name|Description
argument_list|(
literal|"If set to true a full check would be performed which can be slow as "
operator|+
literal|"it reads all index files. If set to false a quick check is performed to "
operator|+
literal|"check if all blobs referred in index files are present in BlobStore"
argument_list|)
name|boolean
name|fullCheck
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Description
argument_list|(
literal|"Performs consistency check for all Lucene indexes and reports true if all indexes are found "
operator|+
literal|"to be valid. False if any one of them was not found to be valid"
argument_list|)
name|boolean
name|checkConsistencyOfAllIndexes
parameter_list|(
annotation|@
name|Name
argument_list|(
literal|"fullCheck"
argument_list|)
annotation|@
name|Description
argument_list|(
literal|"If set to true a full check would be performed which can be slow as "
operator|+
literal|"it reads all index files. If set to false a quick check is performed to "
operator|+
literal|"check if all blobs referred in index files are present in BlobStore"
argument_list|)
name|boolean
name|fullCheck
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

