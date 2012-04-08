begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|model
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
name|mk
operator|.
name|store
operator|.
name|Binding
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|StoredCommit
extends|extends
name|AbstractCommit
block|{
specifier|private
specifier|final
name|Id
name|id
decl_stmt|;
specifier|public
specifier|static
name|StoredCommit
name|deserialize
parameter_list|(
name|Id
name|id
parameter_list|,
name|Binding
name|binding
parameter_list|)
throws|throws
name|Exception
block|{
name|Id
name|rootNodeId
init|=
operator|new
name|Id
argument_list|(
name|binding
operator|.
name|readBytesValue
argument_list|(
literal|"rootNodeId"
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|commitTS
init|=
name|binding
operator|.
name|readLongValue
argument_list|(
literal|"commitTS"
argument_list|)
decl_stmt|;
name|String
name|msg
init|=
name|binding
operator|.
name|readStringValue
argument_list|(
literal|"msg"
argument_list|)
decl_stmt|;
name|String
name|changes
init|=
name|binding
operator|.
name|readStringValue
argument_list|(
literal|"changes"
argument_list|)
decl_stmt|;
name|String
name|parentId
init|=
name|binding
operator|.
name|readStringValue
argument_list|(
literal|"parentId"
argument_list|)
decl_stmt|;
return|return
operator|new
name|StoredCommit
argument_list|(
name|id
argument_list|,
literal|""
operator|.
name|equals
argument_list|(
name|parentId
argument_list|)
condition|?
literal|null
else|:
name|Id
operator|.
name|fromString
argument_list|(
name|parentId
argument_list|)
argument_list|,
name|commitTS
argument_list|,
name|rootNodeId
argument_list|,
literal|""
operator|.
name|equals
argument_list|(
name|msg
argument_list|)
condition|?
literal|null
else|:
name|msg
argument_list|,
name|changes
argument_list|)
return|;
block|}
specifier|public
name|StoredCommit
parameter_list|(
name|Id
name|id
parameter_list|,
name|Id
name|parentId
parameter_list|,
name|long
name|commitTS
parameter_list|,
name|Id
name|rootNodeId
parameter_list|,
name|String
name|msg
parameter_list|,
name|String
name|changes
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|parentId
operator|=
name|parentId
expr_stmt|;
name|this
operator|.
name|commitTS
operator|=
name|commitTS
expr_stmt|;
name|this
operator|.
name|rootNodeId
operator|=
name|rootNodeId
expr_stmt|;
name|this
operator|.
name|msg
operator|=
name|msg
expr_stmt|;
name|this
operator|.
name|changes
operator|=
name|changes
expr_stmt|;
block|}
specifier|public
name|StoredCommit
parameter_list|(
name|Id
name|id
parameter_list|,
name|Commit
name|commit
parameter_list|)
block|{
name|super
argument_list|(
name|commit
argument_list|)
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
specifier|public
name|Id
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
block|}
end_class

end_unit

