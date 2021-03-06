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
name|azure
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
name|azure
operator|.
name|util
operator|.
name|CaseInsensitiveKeysMapAccess
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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

begin_comment
comment|/**  * Provides access to the blob metadata.  *<p>  * In azure blob metadata keys are case-insensitive. A bug in the tool azcopy v10 make each key to start with  * an uppercase letter.  To avoid future bugs we should be tolerant in what we read.  *<p>  * Azure Blobs metadata can not store multiple entries with the same key where only the case differs. Therefore it is  * safe to use the same concept in java, see {@link CaseInsensitiveKeysMapAccess}  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|AzureBlobMetadata
block|{
specifier|static
specifier|final
name|String
name|METADATA_TYPE
init|=
literal|"type"
decl_stmt|;
specifier|static
specifier|final
name|String
name|METADATA_SEGMENT_UUID
init|=
literal|"uuid"
decl_stmt|;
specifier|static
specifier|final
name|String
name|METADATA_SEGMENT_POSITION
init|=
literal|"position"
decl_stmt|;
specifier|static
specifier|final
name|String
name|METADATA_SEGMENT_GENERATION
init|=
literal|"generation"
decl_stmt|;
specifier|static
specifier|final
name|String
name|METADATA_SEGMENT_FULL_GENERATION
init|=
literal|"fullGeneration"
decl_stmt|;
specifier|static
specifier|final
name|String
name|METADATA_SEGMENT_COMPACTED
init|=
literal|"compacted"
decl_stmt|;
specifier|static
specifier|final
name|String
name|TYPE_SEGMENT
init|=
literal|"segment"
decl_stmt|;
specifier|public
specifier|static
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|toSegmentMetadata
parameter_list|(
name|AzureSegmentArchiveEntry
name|indexEntry
parameter_list|)
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|METADATA_TYPE
argument_list|,
name|TYPE_SEGMENT
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|METADATA_SEGMENT_UUID
argument_list|,
operator|new
name|UUID
argument_list|(
name|indexEntry
operator|.
name|getMsb
argument_list|()
argument_list|,
name|indexEntry
operator|.
name|getLsb
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|METADATA_SEGMENT_POSITION
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|indexEntry
operator|.
name|getPosition
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|METADATA_SEGMENT_GENERATION
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|indexEntry
operator|.
name|getGeneration
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|METADATA_SEGMENT_FULL_GENERATION
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|indexEntry
operator|.
name|getFullGeneration
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|METADATA_SEGMENT_COMPACTED
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|indexEntry
operator|.
name|isCompacted
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|map
return|;
block|}
specifier|public
specifier|static
name|AzureSegmentArchiveEntry
name|toIndexEntry
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|caseInsensitiveMetadata
init|=
name|CaseInsensitiveKeysMapAccess
operator|.
name|convert
argument_list|(
name|metadata
argument_list|)
decl_stmt|;
name|UUID
name|uuid
init|=
name|UUID
operator|.
name|fromString
argument_list|(
name|caseInsensitiveMetadata
operator|.
name|get
argument_list|(
name|METADATA_SEGMENT_UUID
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|msb
init|=
name|uuid
operator|.
name|getMostSignificantBits
argument_list|()
decl_stmt|;
name|long
name|lsb
init|=
name|uuid
operator|.
name|getLeastSignificantBits
argument_list|()
decl_stmt|;
name|int
name|position
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|caseInsensitiveMetadata
operator|.
name|get
argument_list|(
name|METADATA_SEGMENT_POSITION
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|generation
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|caseInsensitiveMetadata
operator|.
name|get
argument_list|(
name|METADATA_SEGMENT_GENERATION
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|fullGeneration
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|caseInsensitiveMetadata
operator|.
name|get
argument_list|(
name|METADATA_SEGMENT_FULL_GENERATION
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|compacted
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|caseInsensitiveMetadata
operator|.
name|get
argument_list|(
name|METADATA_SEGMENT_COMPACTED
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|AzureSegmentArchiveEntry
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|,
name|position
argument_list|,
name|length
argument_list|,
name|generation
argument_list|,
name|fullGeneration
argument_list|,
name|compacted
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isSegment
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|caseInsensitiveMetadata
init|=
name|CaseInsensitiveKeysMapAccess
operator|.
name|convert
argument_list|(
name|metadata
argument_list|)
decl_stmt|;
return|return
name|metadata
operator|!=
literal|null
operator|&&
name|TYPE_SEGMENT
operator|.
name|equals
argument_list|(
name|caseInsensitiveMetadata
operator|.
name|get
argument_list|(
name|METADATA_TYPE
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

