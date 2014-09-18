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
name|segment
operator|.
name|failover
operator|.
name|codec
package|;
end_package

begin_class
specifier|public
class|class
name|Messages
block|{
specifier|public
specifier|static
specifier|final
name|byte
name|HEADER_RECORD
init|=
literal|0x00
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|HEADER_SEGMENT
init|=
literal|0x01
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|GET_HEAD
init|=
literal|"h"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|GET_SEGMENT
init|=
literal|"s."
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MAGIC
init|=
literal|"FailOver-CMD@"
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
name|clientID
parameter_list|,
name|String
name|body
parameter_list|)
block|{
return|return
name|MAGIC
operator|+
operator|(
name|clientID
operator|==
literal|null
condition|?
literal|""
else|:
name|clientID
operator|.
name|replace
argument_list|(
name|SEPARATOR
argument_list|,
literal|"#"
argument_list|)
operator|)
operator|+
name|SEPARATOR
operator|+
name|body
operator|+
literal|"\r\n"
return|;
block|}
specifier|public
specifier|static
name|String
name|newGetHeadReq
parameter_list|(
name|String
name|clientID
parameter_list|)
block|{
return|return
name|newRequest
argument_list|(
name|clientID
argument_list|,
name|GET_HEAD
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|newGetSegmentReq
parameter_list|(
name|String
name|clientID
parameter_list|,
name|String
name|sid
parameter_list|)
block|{
return|return
name|newRequest
argument_list|(
name|clientID
argument_list|,
name|GET_SEGMENT
operator|+
name|sid
argument_list|)
return|;
block|}
specifier|public
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
specifier|public
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

