package com.huytpq.SecurityEx.recipe.entity;

import com.huytpq.SecurityEx.base.data.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "recipe_images")
public class RecipeImage extends BaseEntity {
    public static final int MAXIMUM_IMAGES_PER_RECIPE = 5;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

//    @ManyToOne
//    @JoinColumn(name = "post_id")
//    private Post post;

}
