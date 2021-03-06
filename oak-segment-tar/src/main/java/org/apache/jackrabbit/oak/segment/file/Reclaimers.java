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
name|file
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|base
operator|.
name|Predicate
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
name|segment
operator|.
name|compaction
operator|.
name|SegmentGCOptions
operator|.
name|GCType
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
name|segment
operator|.
name|file
operator|.
name|tar
operator|.
name|GCGeneration
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
name|NotNull
import|;
end_import

begin_comment
comment|/**  * Helper class exposing static factories for reclaimers. A reclaimer  * is a predicate used during the cleanup phase of garbage collection  * to decide whether a segment of a given generation is reclaimable.  */
end_comment

begin_class
class|class
name|Reclaimers
block|{
specifier|private
name|Reclaimers
parameter_list|()
block|{
comment|// Prevent instantiation.
block|}
comment|/**      * Create a reclaimer for segments of old generations. Whether a segment is considered old and      * thus reclaimable depends on the type of the most recent GC operation and the number of      * retained generations.      *<p>      * In the case of {@link GCType#FULL FULL} a segment is reclaimable if its      * {@link GCGeneration#getFullGeneration() full generation} is at least {@code retainedGenerations}      * in the past wrt. {@code referenceGeneration}<em>or</em> if its      * {@link GCGeneration#getGeneration() generation} is at least {@code retainedGenerations} in the      * past wrt. {@code referenceGeneration} and it not a {@link GCGeneration#isCompacted() compacted segment}.      *<p>      * In the case of {@link GCType#TAIL TAIL} a segment is reclaimable if its      * {@link GCGeneration#getGeneration() generation} is at least {@code retainedGenerations} in the      * past wrt. {@code referenceGeneration}<em>and</em> the segment is not in the same tail as      * segments of the {@code referenceGeneration}. A segment is in the same tail as another segment      * if it is a {@link GCGeneration#isCompacted() compacted segment}<em>and</em> both segments have      * the same {@code full generation}.      *      * @param lastGCType  type of the most recent GC operation. {@link GCType#FULL} if unknown.      * @param referenceGeneration  generation used as reference for determining the age of other segments.      * @param retainedGenerations  number of generations to retain.      */
specifier|static
name|Predicate
argument_list|<
name|GCGeneration
argument_list|>
name|newOldReclaimer
parameter_list|(
annotation|@
name|NotNull
name|GCType
name|lastGCType
parameter_list|,
annotation|@
name|NotNull
specifier|final
name|GCGeneration
name|referenceGeneration
parameter_list|,
name|int
name|retainedGenerations
parameter_list|)
block|{
switch|switch
condition|(
name|checkNotNull
argument_list|(
name|lastGCType
argument_list|)
condition|)
block|{
case|case
name|FULL
case|:
return|return
name|newOldFullReclaimer
argument_list|(
name|referenceGeneration
argument_list|,
name|retainedGenerations
argument_list|)
return|;
case|case
name|TAIL
case|:
return|return
name|newOldTailReclaimer
argument_list|(
name|referenceGeneration
argument_list|,
name|retainedGenerations
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid gc type: "
operator|+
name|lastGCType
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|Predicate
argument_list|<
name|GCGeneration
argument_list|>
name|newOldFullReclaimer
parameter_list|(
annotation|@
name|NotNull
specifier|final
name|GCGeneration
name|referenceGeneration
parameter_list|,
name|int
name|retainedGenerations
parameter_list|)
block|{
return|return
operator|new
name|Predicate
argument_list|<
name|GCGeneration
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|GCGeneration
name|generation
parameter_list|)
block|{
return|return
name|isOldFull
argument_list|(
name|generation
argument_list|)
operator|||
operator|(
name|isOld
argument_list|(
name|generation
argument_list|)
operator|&&
operator|!
name|generation
operator|.
name|isCompacted
argument_list|()
operator|)
return|;
block|}
specifier|private
name|boolean
name|isOld
parameter_list|(
name|GCGeneration
name|generation
parameter_list|)
block|{
return|return
name|referenceGeneration
operator|.
name|compareWith
argument_list|(
name|generation
argument_list|)
operator|>=
name|retainedGenerations
return|;
block|}
specifier|private
name|boolean
name|isOldFull
parameter_list|(
name|GCGeneration
name|generation
parameter_list|)
block|{
return|return
name|referenceGeneration
operator|.
name|compareFullGenerationWith
argument_list|(
name|generation
argument_list|)
operator|>=
name|retainedGenerations
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"(full generation older than %d.%d, with %d retained generations)"
argument_list|,
name|referenceGeneration
operator|.
name|getGeneration
argument_list|()
argument_list|,
name|referenceGeneration
operator|.
name|getFullGeneration
argument_list|()
argument_list|,
name|retainedGenerations
argument_list|)
return|;
block|}
block|}
return|;
block|}
specifier|private
specifier|static
name|Predicate
argument_list|<
name|GCGeneration
argument_list|>
name|newOldTailReclaimer
parameter_list|(
annotation|@
name|NotNull
specifier|final
name|GCGeneration
name|referenceGeneration
parameter_list|,
name|int
name|retainedGenerations
parameter_list|)
block|{
return|return
operator|new
name|Predicate
argument_list|<
name|GCGeneration
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|GCGeneration
name|generation
parameter_list|)
block|{
return|return
name|isOld
argument_list|(
name|generation
argument_list|)
operator|&&
operator|!
name|sameCompactedTail
argument_list|(
name|generation
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|isOld
parameter_list|(
name|GCGeneration
name|generation
parameter_list|)
block|{
return|return
name|referenceGeneration
operator|.
name|compareWith
argument_list|(
name|generation
argument_list|)
operator|>=
name|retainedGenerations
return|;
block|}
specifier|private
name|boolean
name|sameCompactedTail
parameter_list|(
name|GCGeneration
name|generation
parameter_list|)
block|{
return|return
name|generation
operator|.
name|isCompacted
argument_list|()
operator|&&
name|generation
operator|.
name|getFullGeneration
argument_list|()
operator|==
name|referenceGeneration
operator|.
name|getFullGeneration
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"(generation older than %d.%d, with %d retained generations and not in the same compacted tail)"
argument_list|,
name|referenceGeneration
operator|.
name|getGeneration
argument_list|()
argument_list|,
name|referenceGeneration
operator|.
name|getFullGeneration
argument_list|()
argument_list|,
name|retainedGenerations
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**      * Create an exact reclaimer. An exact reclaimer reclaims only segment of on single generation.      * @param referenceGeneration  the generation to collect.      * @return  an new instance of an exact reclaimer for segments with their generation      *          matching {@code referenceGeneration}.      */
specifier|static
name|Predicate
argument_list|<
name|GCGeneration
argument_list|>
name|newExactReclaimer
parameter_list|(
annotation|@
name|NotNull
specifier|final
name|GCGeneration
name|referenceGeneration
parameter_list|)
block|{
return|return
operator|new
name|Predicate
argument_list|<
name|GCGeneration
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|GCGeneration
name|generation
parameter_list|)
block|{
return|return
name|generation
operator|.
name|equals
argument_list|(
name|referenceGeneration
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"(generation=="
operator|+
name|referenceGeneration
operator|+
literal|")"
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

