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
name|property
operator|.
name|jmx
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
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
name|OpenDataException
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
name|commons
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
name|commons
operator|.
name|jmx
operator|.
name|Name
import|;
end_import

begin_interface
specifier|public
interface|interface
name|PropertyIndexStatsMBean
block|{
name|String
name|TYPE
init|=
literal|"PropertyIndexStats"
decl_stmt|;
annotation|@
name|Description
argument_list|(
literal|"Determines statistics related to specific property index which can be used to optimize property index "
operator|+
literal|"definition. Various limits below are provided to ensure that estimation logic does not consume too much "
operator|+
literal|"resources. If any limits are reached then report would not be considered conclusive and would not have "
operator|+
literal|"paths set determined"
argument_list|)
name|CompositeData
name|getStatsForSpecificIndex
parameter_list|(
annotation|@
name|Name
argument_list|(
literal|"indexPath"
argument_list|)
annotation|@
name|Description
argument_list|(
literal|"Index path for which stats are to be determined"
argument_list|)
name|String
name|path
parameter_list|,
annotation|@
name|Name
argument_list|(
literal|"maxValueCount"
argument_list|)
annotation|@
name|Description
argument_list|(
literal|"Maximum number of values to examine. E.g. 100. Stats calculation would "
operator|+
literal|"break out after this limit"
argument_list|)
name|int
name|maxValueCount
parameter_list|,
annotation|@
name|Description
argument_list|(
literal|"Maximum depth to examine. E.g. 5. Stats calculation would "
operator|+
literal|"break out after this limit"
argument_list|)
annotation|@
name|Name
argument_list|(
literal|"maxDepth"
argument_list|)
name|int
name|maxDepth
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
name|OpenDataException
function_decl|;
annotation|@
name|Description
argument_list|(
literal|"Determines statistics related to property index for all property index under given path. Various limits "
operator|+
literal|"below are provided to ensure that estimation logic does not consume too much "
operator|+
literal|"resources. If any limits are reached then report would not be considered conclusive and would not have "
operator|+
literal|"paths set determined"
argument_list|)
name|TabularData
name|getStatsForAllIndexes
parameter_list|(
annotation|@
name|Name
argument_list|(
literal|"path"
argument_list|)
annotation|@
name|Description
argument_list|(
literal|"Path under which all indexes are to be examined like '/'. Path should not "
operator|+
literal|"include oak:index"
argument_list|)
name|String
name|path
parameter_list|,
annotation|@
name|Name
argument_list|(
literal|"maxValueCount"
argument_list|)
annotation|@
name|Description
argument_list|(
literal|"Maximum number of values to examine. E.g. 100. Stats calculation would "
operator|+
literal|"break out after this limit"
argument_list|)
name|int
name|maxValueCount
parameter_list|,
annotation|@
name|Description
argument_list|(
literal|"Maximum depth to examine. E.g. 5. Stats calculation would "
operator|+
literal|"break out after this limit"
argument_list|)
annotation|@
name|Name
argument_list|(
literal|"maxDepth"
argument_list|)
name|int
name|maxDepth
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
name|OpenDataException
function_decl|;
block|}
end_interface

end_unit

