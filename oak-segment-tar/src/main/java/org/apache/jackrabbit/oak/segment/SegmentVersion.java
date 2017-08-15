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
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|max
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|EnumSet
operator|.
name|allOf
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|primitives
operator|.
name|UnsignedBytes
import|;
end_import

begin_comment
comment|/**  * Version of the segment storage format.<ul><li>12 = all oak-segment-tar  * versions</li></ul>  */
end_comment

begin_enum
specifier|public
enum|enum
name|SegmentVersion
block|{
comment|/*      * ON OLDER VERSIONS      *      * The legacy Segment Store implemented in oak-segment makes use of version      * numbers 10 and 11. These version numbers identify two variations of the      * data format that oak-segment can parse and understand.      *      * For oak-segment-tar 10 and 11 are invalid values for the segment version.      * The data format identified by these versions is not understood by      * oak-segment-tar. No special handling is needed for versions 10 and 11.      * From the perspective of oak-segment-tar, they are just invalid. The first      * valid version for oak-segment-tar is 12.      *      * As a consequence, if you find yourself debugging code from      * oak-segment-tar and you detect that version 10 or 11 is used in some      * segment, you are probably trying to read the old data format with the new      * code.      */
name|V_12
argument_list|(
operator|(
name|byte
operator|)
literal|12
argument_list|)
block|,
name|V_13
argument_list|(
operator|(
name|byte
operator|)
literal|13
argument_list|)
block|;
comment|/**      * Latest segment version      */
specifier|public
specifier|static
specifier|final
name|SegmentVersion
name|LATEST_VERSION
init|=
name|max
argument_list|(
name|allOf
argument_list|(
name|SegmentVersion
operator|.
name|class
argument_list|)
argument_list|,
parameter_list|(
name|v1
parameter_list|,
name|v2
parameter_list|)
lambda|->
name|UnsignedBytes
operator|.
name|compare
argument_list|(
name|v1
operator|.
name|version
argument_list|,
name|v2
operator|.
name|version
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|byte
name|version
decl_stmt|;
name|SegmentVersion
parameter_list|(
name|byte
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
block|}
specifier|public
specifier|static
name|byte
name|asByte
parameter_list|(
name|SegmentVersion
name|v
parameter_list|)
block|{
return|return
name|v
operator|.
name|version
return|;
block|}
specifier|public
specifier|static
name|SegmentVersion
name|fromByte
parameter_list|(
name|byte
name|v
parameter_list|)
block|{
if|if
condition|(
name|v
operator|==
name|V_13
operator|.
name|version
condition|)
block|{
return|return
name|V_13
return|;
block|}
if|if
condition|(
name|v
operator|==
name|V_12
operator|.
name|version
condition|)
block|{
return|return
name|V_12
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown version "
operator|+
name|v
argument_list|)
throw|;
block|}
specifier|public
specifier|static
name|boolean
name|isValid
parameter_list|(
name|byte
name|v
parameter_list|)
block|{
return|return
name|v
operator|==
name|V_13
operator|.
name|version
operator|||
name|v
operator|==
name|V_12
operator|.
name|version
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isValid
parameter_list|(
name|SegmentVersion
name|version
parameter_list|)
block|{
return|return
name|isValid
argument_list|(
name|version
operator|.
name|version
argument_list|)
return|;
block|}
block|}
end_enum

end_unit

