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
name|standby
operator|.
name|codec
package|;
end_package

begin_class
class|class
name|Messages
block|{
specifier|static
specifier|final
name|byte
name|HEADER_RECORD
init|=
literal|0x00
decl_stmt|;
specifier|static
specifier|final
name|byte
name|HEADER_SEGMENT
init|=
literal|0x01
decl_stmt|;
specifier|static
specifier|final
name|byte
name|HEADER_BLOB
init|=
literal|0x02
decl_stmt|;
specifier|static
specifier|final
name|String
name|GET_HEAD
init|=
literal|"h"
decl_stmt|;
specifier|static
specifier|final
name|String
name|GET_SEGMENT
init|=
literal|"s."
decl_stmt|;
specifier|static
specifier|final
name|String
name|GET_BLOB
init|=
literal|"b."
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MAGIC
init|=
literal|"Standby-CMD@"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|SEPARATOR
init|=
literal|":"
decl_stmt|;
specifier|private
specifier|static
name|String
name|newRequest
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|body
parameter_list|,
name|boolean
name|delimited
parameter_list|)
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|(
name|MAGIC
argument_list|)
decl_stmt|;
if|if
condition|(
name|clientId
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|clientId
operator|.
name|replace
argument_list|(
name|SEPARATOR
argument_list|,
literal|"#"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
name|SEPARATOR
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|body
argument_list|)
expr_stmt|;
if|if
condition|(
name|delimited
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"\r\n"
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|static
name|String
name|newGetHeadRequest
parameter_list|(
name|String
name|clientId
parameter_list|,
name|boolean
name|delimited
parameter_list|)
block|{
return|return
name|newRequest
argument_list|(
name|clientId
argument_list|,
name|GET_HEAD
argument_list|,
name|delimited
argument_list|)
return|;
block|}
specifier|static
name|String
name|newGetHeadRequest
parameter_list|(
name|String
name|clientId
parameter_list|)
block|{
return|return
name|newGetHeadRequest
argument_list|(
name|clientId
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|static
name|String
name|newGetSegmentRequest
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|segmentId
parameter_list|,
name|boolean
name|delimited
parameter_list|)
block|{
return|return
name|newRequest
argument_list|(
name|clientId
argument_list|,
name|GET_SEGMENT
operator|+
name|segmentId
argument_list|,
name|delimited
argument_list|)
return|;
block|}
specifier|static
name|String
name|newGetSegmentRequest
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|segmentId
parameter_list|)
block|{
return|return
name|newGetSegmentRequest
argument_list|(
name|clientId
argument_list|,
name|segmentId
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|static
name|String
name|newGetBlobRequest
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|blobId
parameter_list|,
name|boolean
name|delimited
parameter_list|)
block|{
return|return
name|newRequest
argument_list|(
name|clientId
argument_list|,
name|GET_BLOB
operator|+
name|blobId
argument_list|,
name|delimited
argument_list|)
return|;
block|}
specifier|static
name|String
name|newGetBlobRequest
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|blobId
parameter_list|)
block|{
return|return
name|newGetBlobRequest
argument_list|(
name|clientId
argument_list|,
name|blobId
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|static
name|String
name|extractMessageFrom
parameter_list|(
name|String
name|payload
parameter_list|)
block|{
if|if
condition|(
name|payload
operator|.
name|startsWith
argument_list|(
name|MAGIC
argument_list|)
operator|&&
name|payload
operator|.
name|length
argument_list|()
operator|>
name|MAGIC
operator|.
name|length
argument_list|()
condition|)
block|{
name|int
name|i
init|=
name|payload
operator|.
name|indexOf
argument_list|(
name|SEPARATOR
argument_list|)
decl_stmt|;
return|return
name|payload
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|static
name|String
name|extractClientFrom
parameter_list|(
name|String
name|payload
parameter_list|)
block|{
if|if
condition|(
name|payload
operator|.
name|startsWith
argument_list|(
name|MAGIC
argument_list|)
operator|&&
name|payload
operator|.
name|length
argument_list|()
operator|>
name|MAGIC
operator|.
name|length
argument_list|()
condition|)
block|{
name|payload
operator|=
name|payload
operator|.
name|substring
argument_list|(
name|MAGIC
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|i
init|=
name|payload
operator|.
name|indexOf
argument_list|(
name|SEPARATOR
argument_list|)
decl_stmt|;
return|return
name|payload
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

