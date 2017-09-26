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
name|plugins
operator|.
name|document
operator|.
name|mongo
package|;
end_package

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|BasicDBObject
import|;
end_import

begin_comment
comment|/**  * The {@code MongoDB} representation of a blob. Only used by MongoBlobStore  */
end_comment

begin_class
specifier|public
class|class
name|MongoBlob
extends|extends
name|BasicDBObject
block|{
specifier|public
specifier|static
specifier|final
name|String
name|KEY_ID
init|=
literal|"_id"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_DATA
init|=
literal|"data"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_LAST_MOD
init|=
literal|"lastMod"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_LEVEL
init|=
literal|"level"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|5119970546251968672L
decl_stmt|;
comment|/**      * Default constructor. Needed for MongoDB serialization.      */
specifier|public
name|MongoBlob
parameter_list|()
block|{     }
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|getString
argument_list|(
name|KEY_ID
argument_list|)
return|;
block|}
specifier|public
name|void
name|setId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|put
argument_list|(
name|KEY_ID
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
specifier|public
name|byte
index|[]
name|getData
parameter_list|()
block|{
return|return
operator|(
name|byte
index|[]
operator|)
name|get
argument_list|(
name|KEY_DATA
argument_list|)
return|;
block|}
specifier|public
name|void
name|setData
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
name|put
argument_list|(
name|KEY_DATA
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getLevel
parameter_list|()
block|{
return|return
name|getInt
argument_list|(
name|KEY_LEVEL
argument_list|)
return|;
block|}
specifier|public
name|void
name|setLevel
parameter_list|(
name|int
name|level
parameter_list|)
block|{
name|put
argument_list|(
name|KEY_LEVEL
argument_list|,
name|level
argument_list|)
expr_stmt|;
block|}
specifier|public
name|long
name|getLastMod
parameter_list|()
block|{
return|return
name|getLong
argument_list|(
name|KEY_LAST_MOD
argument_list|)
return|;
block|}
specifier|public
name|void
name|setLastMod
parameter_list|(
name|long
name|lastMod
parameter_list|)
block|{
name|put
argument_list|(
name|KEY_LAST_MOD
argument_list|,
name|lastMod
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
