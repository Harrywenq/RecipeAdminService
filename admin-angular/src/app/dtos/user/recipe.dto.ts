import {
    IsString,
    IsNotEmpty,
    IsDate
} from 'class-validator';

export class RecipeDto {
    @IsNotEmpty()
    id: number;

    @IsString()
    @IsNotEmpty()
    name: string;

    @IsString()
    description: string;

    @IsNotEmpty()
    thumbnail: Date;

    @IsNotEmpty()
    user: number = 1;

    @IsNotEmpty()
    recipe_category: number = 1;

    @IsString()
    url: string;

    constructor(data: any) {
        this.id = data.id;
        this.name = data.name;
        this.description = data.description;
        this.thumbnail = data.thumbnail;
        this.user = data.user || 1;
        this.recipe_category = data.recipe_category || 1;
        this.url = data.url;
    }

}