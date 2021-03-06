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
name|rdb
package|;
end_package

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Container for the information in a RDB database column.  *<p>  * Note that the String "data" and the byte[] "bdata" may be {@code null} when  * the SQL SELECT request was conditional on "modcount" being unchanged.  */
end_comment

begin_class
specifier|public
class|class
name|RDBRow
block|{
specifier|public
specifier|static
specifier|final
name|long
name|LONG_UNSET
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
specifier|private
specifier|final
name|String
name|id
decl_stmt|;
specifier|private
specifier|final
name|Long
name|hasBinaryProperties
decl_stmt|;
specifier|private
specifier|final
name|Boolean
name|deletedOnce
decl_stmt|;
specifier|private
specifier|final
name|long
name|modified
decl_stmt|,
name|modcount
decl_stmt|,
name|cmodcount
decl_stmt|;
specifier|private
specifier|final
name|long
name|schemaVersion
decl_stmt|;
specifier|private
specifier|final
name|long
name|sdType
decl_stmt|,
name|sdMaxRevTime
decl_stmt|;
specifier|private
specifier|final
name|String
name|data
decl_stmt|;
specifier|private
specifier|final
name|byte
index|[]
name|bdata
decl_stmt|;
specifier|public
name|RDBRow
parameter_list|(
name|String
name|id
parameter_list|,
name|Long
name|hasBinaryProperties
parameter_list|,
name|Boolean
name|deletedOnce
parameter_list|,
name|Long
name|modified
parameter_list|,
name|Long
name|modcount
parameter_list|,
name|Long
name|cmodcount
parameter_list|,
name|Long
name|schemaVersion
parameter_list|,
name|Long
name|sdType
parameter_list|,
name|Long
name|sdMaxRevTime
parameter_list|,
name|String
name|data
parameter_list|,
name|byte
index|[]
name|bdata
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
name|hasBinaryProperties
operator|=
name|hasBinaryProperties
expr_stmt|;
name|this
operator|.
name|deletedOnce
operator|=
name|deletedOnce
expr_stmt|;
name|this
operator|.
name|modified
operator|=
name|modified
operator|!=
literal|null
condition|?
name|modified
operator|.
name|longValue
argument_list|()
else|:
name|LONG_UNSET
expr_stmt|;
name|this
operator|.
name|modcount
operator|=
name|modcount
operator|!=
literal|null
condition|?
name|modcount
operator|.
name|longValue
argument_list|()
else|:
name|LONG_UNSET
expr_stmt|;
name|this
operator|.
name|cmodcount
operator|=
name|cmodcount
operator|!=
literal|null
condition|?
name|cmodcount
operator|.
name|longValue
argument_list|()
else|:
name|LONG_UNSET
expr_stmt|;
name|this
operator|.
name|schemaVersion
operator|=
name|schemaVersion
operator|!=
literal|null
condition|?
name|schemaVersion
operator|.
name|longValue
argument_list|()
else|:
name|LONG_UNSET
expr_stmt|;
name|this
operator|.
name|sdType
operator|=
name|sdType
operator|!=
literal|null
condition|?
name|sdType
operator|.
name|longValue
argument_list|()
else|:
name|LONG_UNSET
expr_stmt|;
name|this
operator|.
name|sdMaxRevTime
operator|=
name|sdMaxRevTime
operator|!=
literal|null
condition|?
name|sdMaxRevTime
operator|.
name|longValue
argument_list|()
else|:
name|LONG_UNSET
expr_stmt|;
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
name|this
operator|.
name|bdata
operator|=
name|bdata
expr_stmt|;
block|}
annotation|@
name|NotNull
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
annotation|@
name|Nullable
specifier|public
name|Long
name|hasBinaryProperties
parameter_list|()
block|{
return|return
name|hasBinaryProperties
return|;
block|}
annotation|@
name|Nullable
specifier|public
name|Boolean
name|deletedOnce
parameter_list|()
block|{
return|return
name|deletedOnce
return|;
block|}
annotation|@
name|Nullable
specifier|public
name|String
name|getData
parameter_list|()
block|{
return|return
name|data
return|;
block|}
comment|/**      * @return {@link #LONG_UNSET} when not set in the database      */
specifier|public
name|long
name|getModified
parameter_list|()
block|{
return|return
name|modified
return|;
block|}
comment|/**      * @return {@link #LONG_UNSET} when not set in the database      */
specifier|public
name|long
name|getModcount
parameter_list|()
block|{
return|return
name|modcount
return|;
block|}
comment|/**      * @return {@link #LONG_UNSET} when not set in the database      */
specifier|public
name|long
name|getCollisionsModcount
parameter_list|()
block|{
return|return
name|cmodcount
return|;
block|}
comment|/**      * @return {@link #LONG_UNSET} when not set in the database      */
specifier|public
name|long
name|getSchemaVersion
parameter_list|()
block|{
return|return
name|schemaVersion
return|;
block|}
comment|/**      * @return {@link #LONG_UNSET} when not set in the database      */
specifier|public
name|long
name|getSdType
parameter_list|()
block|{
return|return
name|sdType
return|;
block|}
comment|/**      * @return {@link #LONG_UNSET} when not set in the database      */
specifier|public
name|long
name|getSdMaxRevTime
parameter_list|()
block|{
return|return
name|sdMaxRevTime
return|;
block|}
annotation|@
name|Nullable
specifier|public
name|byte
index|[]
name|getBdata
parameter_list|()
block|{
return|return
name|bdata
return|;
block|}
block|}
end_class

end_unit

